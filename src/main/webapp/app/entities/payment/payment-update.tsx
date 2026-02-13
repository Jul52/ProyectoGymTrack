import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams, useLocation } from 'react-router-dom';
import { Button, Col, FormText, Row, Spinner } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getPaymentMethods } from 'app/entities/payment-method/payment-method.reducer';
import { getEntities as getUserData } from 'app/entities/user-data/user-data.reducer';
import { getEntity as getGymService } from 'app/entities/gym-service/gym-service.reducer';

import { createEntity, getEntity, reset, updateEntity } from './payment.reducer';
import { AUTHORITIES } from 'app/config/constants';

export const PaymentUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const [processing, setProcessing] = useState(false);

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const queryParams = new URLSearchParams(location.search);
  const serviceId = queryParams.get('serviceId');

  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = account.authorities.includes(AUTHORITIES.ADMIN);

  const paymentMethods = useAppSelector(state => state.paymentMethod.entities);
  const userData = useAppSelector(state => state.userData.entities);
  const paymentEntity = useAppSelector(state => state.payment.entity);
  const gymService = useAppSelector(state => state.gymService.entity);

  const loading = useAppSelector(state => state.payment.loading);
  const updating = useAppSelector(state => state.payment.updating);
  const updateSuccess = useAppSelector(state => state.payment.updateSuccess);

  const handleClose = () => {
    navigate('/payment');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPaymentMethods({}));

    if (isAdmin) {
      dispatch(getUserData({}));
    }

    if (serviceId) {
      dispatch(getGymService(serviceId));
    }
  }, [isAdmin, isNew, serviceId]);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.amountPaid !== undefined && typeof values.amountPaid !== 'number') {
      values.amountPaid = Number(values.amountPaid);
    }

    setProcessing(true);

    // 🔥 Simulación de pasarela (2 segundos)
    setTimeout(() => {
      const fakeTransactionId = 'TX-' + Date.now();

      const entity = {
        ...paymentEntity,
        ...values,

        // ✅ Forzamos estado para evitar error 500
        status: 'PAID',

        // ✅ ID de transacción simulada
        transactionId: fakeTransactionId,

        paymentDate: convertDateTimeToServer(values.paymentDate),

        gymService: serviceId ? { id: Number(serviceId) } : paymentEntity.gymService,

        paymentMethod: paymentMethods.find(it => it.id.toString() === values.paymentMethod?.toString()),

        // ✅ Si no es admin, usar usuario logueado
        registeredBy: isAdmin ? userData.find(it => it.id.toString() === values.registeredBy?.toString()) : { id: account.id },
      };

      if (isNew) {
        delete entity.id;
        dispatch(createEntity(entity));
      } else {
        dispatch(updateEntity(entity));
      }

      setProcessing(false);
    }, 2000);
  };

  const defaultValues = () =>
    isNew
      ? {
          paymentDate: displayDefaultDateTime(),
          amountPaid: gymService?.price,
          paymentMethod: '',
        }
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
          <h2>Procesar Pago</h2>
        </Col>
      </Row>

      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Cargando...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" readOnly label="ID" />}
              {/* MONTO AUTOMÁTICO */}
              <ValidatedField
                label="Monto"
                name="amountPaid"
                type="number"
                readOnly
                validate={{
                  required: { value: true, message: 'Campo obligatorio' },
                  validate: v => isNumber(v) || 'Debe ser número',
                }}
              />
              {/* FECHA AUTOMÁTICA */}
              <ValidatedField
                label="Fecha"
                name="paymentDate"
                type="datetime-local"
                readOnly
                validate={{
                  required: { value: true, message: 'Campo obligatorio' },
                }}
              />
              {/* MÉTODO DE PAGO */}
              <ValidatedField name="paymentMethod" label="Método de Pago" type="select" required>
                <option value="" key="0" />
                {paymentMethods.map(pm => (
                  <option value={pm.id} key={pm.id}>
                    {pm.methodName}
                  </option>
                ))}
              </ValidatedField>
              <FormText>Este campo es obligatorio.</FormText>
              <Button tag={Link} to="/payment" replace color="info">
                <FontAwesomeIcon icon="arrow-left" /> Volver
              </Button>
              &nbsp;
              <Button color="success" type="submit" disabled={processing || updating}>
                {processing ? (
                  <>
                    <Spinner size="sm" /> Procesando...
                  </>
                ) : (
                  <>
                    <FontAwesomeIcon icon="credit-card" /> Pagar
                  </>
                )}
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default PaymentUpdate;
