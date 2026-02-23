import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { Button, Card, CardBody, Col, FormText, Row, Spinner } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { getEntities as getPaymentMethods } from 'app/entities/payment-method/payment-method.reducer';
import { getEntities as getUserData } from 'app/entities/user-data/user-data.reducer';
import { createEntity, getEntity, reset, updateEntity } from './payment.reducer';

// ─── Formulario checkout para usuarios normales ───────────────────────────────
const UserCheckoutForm = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const serviceId = searchParams.get('serviceId');
  const price = searchParams.get('price');
  const serviceName = searchParams.get('serviceName') ?? 'Servicio';

  const [selectedMethod, setSelectedMethod] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const paymentMethods = useAppSelector(state => state.paymentMethod.entities);

  useEffect(() => {
    dispatch(getPaymentMethods({}));
  }, []);

  const handleCheckout = async () => {
    if (!selectedMethod) {
      setError('Selecciona un método de pago');
      return;
    }
    setSubmitting(true);
    setError('');
    try {
      await axios.post('/api/payments/checkout', {
        serviceId: Number(serviceId),
        paymentMethodId: Number(selectedMethod),
      });
      navigate('/payment/success');
    } catch {
      setError('Error al procesar el pago. Intenta de nuevo.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Row className="justify-content-center mt-4">
      <Col md="6">
        <Card className="shadow-sm">
          <CardBody>
            <h4 className="mb-4">
              <FontAwesomeIcon icon="shopping-cart" className="me-2" />
              Confirmar pago
            </h4>

            <div className="mb-3 p-3 bg-light rounded">
              <div className="d-flex justify-content-between mb-2">
                <span className="text-muted">Servicio</span>
                <strong>{serviceName}</strong>
              </div>
              <hr className="my-2" />
              <div className="d-flex justify-content-between">
                <span className="text-muted">Total a pagar</span>
                <strong className="text-success fs-5">${price}</strong>
              </div>
            </div>

            <div className="mb-3">
              <label className="form-label fw-bold">Método de pago</label>
              <select className="form-select" value={selectedMethod} onChange={e => setSelectedMethod(e.target.value)}>
                <option value="">-- Selecciona un método --</option>
                {paymentMethods.map(m => (
                  <option key={m.id} value={m.id}>
                    {m.methodName}
                  </option>
                ))}
              </select>
            </div>

            {error && <div className="alert alert-danger py-2">{error}</div>}

            <div className="d-flex gap-2 mt-4">
              <Button color="secondary" tag={Link} to="/gym-service" disabled={submitting}>
                <FontAwesomeIcon icon="arrow-left" /> Cancelar
              </Button>
              <Button color="success" onClick={handleCheckout} disabled={submitting} className="flex-grow-1">
                {submitting ? (
                  <Spinner size="sm" />
                ) : (
                  <>
                    <FontAwesomeIcon icon="check" className="me-1" />
                    Pagar ahora
                  </>
                )}
              </Button>
            </div>
          </CardBody>
        </Card>
      </Col>
    </Row>
  );
};

// ─── Formulario completo para ADMIN ──────────────────────────────────────────
const AdminPaymentForm = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const paymentMethods = useAppSelector(state => state.paymentMethod.entities);
  const userData = useAppSelector(state => state.userData.entities);
  const paymentEntity = useAppSelector(state => state.payment.entity);
  const loading = useAppSelector(state => state.payment.loading);
  const updating = useAppSelector(state => state.payment.updating);
  const updateSuccess = useAppSelector(state => state.payment.updateSuccess);

  useEffect(() => {
    if (isNew) dispatch(reset());
    else dispatch(getEntity(id));
    dispatch(getPaymentMethods({}));
    dispatch(getUserData({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) navigate('/payment');
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.amountPaid !== undefined && typeof values.amountPaid !== 'number') values.amountPaid = Number(values.amountPaid);
    values.paymentDate = convertDateTimeToServer(values.paymentDate);
    const entity = {
      ...paymentEntity,
      ...values,
      paymentMethod: paymentMethods.find(it => it.id.toString() === values.paymentMethod?.toString()),
      registeredBy: userData.find(it => it.id.toString() === values.registeredBy?.toString()),
    };
    if (isNew) dispatch(createEntity(entity));
    else dispatch(updateEntity(entity));
  };

  const defaultValues = () =>
    isNew
      ? { paymentDate: displayDefaultDateTime() }
      : {
          ...paymentEntity,
          paymentDate: convertDateTimeFromServer(paymentEntity.paymentDate),
          paymentMethod: paymentEntity?.paymentMethod?.id,
          registeredBy: paymentEntity?.registeredBy?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2>
            <Translate contentKey="gymtrackApp.payment.home.createOrEditLabel" />
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="payment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('gymtrackApp.payment.amountPaid')}
                id="payment-amountPaid"
                name="amountPaid"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.payment.paymentDate')}
                id="payment-paymentDate"
                name="paymentDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{ required: { value: true, message: translate('entity.validation.required') } }}
              />
              <ValidatedField
                label={translate('gymtrackApp.payment.transactionId')}
                id="payment-transactionId"
                name="transactionId"
                type="text"
                validate={{ maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) } }}
              />
              <ValidatedField
                label={translate('gymtrackApp.payment.status')}
                id="payment-status"
                name="status"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                id="payment-paymentMethod"
                name="paymentMethod"
                type="select"
                required
                label={translate('gymtrackApp.payment.paymentMethod')}
              >
                <option value="" key="0" />
                {paymentMethods?.map(m => (
                  <option value={m.id} key={m.id}>
                    {m.methodName}
                  </option>
                ))}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required" />
              </FormText>
              <ValidatedField
                id="payment-registeredBy"
                name="registeredBy"
                type="select"
                required
                label={translate('gymtrackApp.payment.registeredBy')}
              >
                <option value="" key="0" />
                {userData?.map(u => (
                  <option value={u.id} key={u.id}>
                    {u.document}
                  </option>
                ))}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required" />
              </FormText>
              <Button tag={Link} to="/payment" color="info">
                <FontAwesomeIcon icon="arrow-left" /> <Translate contentKey="entity.action.back" />
              </Button>
              &nbsp;
              <Button color="primary" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" /> <Translate contentKey="entity.action.save" />
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

// ─── Componente principal — decide qué formulario mostrar ────────────────────
export const PaymentUpdate = () => {
  const isAdmin = useAppSelector(state => state.authentication.account.authorities?.includes(AUTHORITIES.ADMIN));
  return isAdmin ? <AdminPaymentForm /> : <UserCheckoutForm />;
};

export default PaymentUpdate;
