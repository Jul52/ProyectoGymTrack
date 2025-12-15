import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getPaymentMethods } from 'app/entities/payment-method/payment-method.reducer';
import { getEntities as getUserData } from 'app/entities/user-data/user-data.reducer';
import { createEntity, getEntity, reset, updateEntity } from './payment.reducer';
import { AUTHORITIES } from 'app/config/constants';

export const PaymentUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = account.authorities.includes(AUTHORITIES.ADMIN);

  const paymentMethods = useAppSelector(state => state.paymentMethod.entities);
  const userData = useAppSelector(state => state.userData.entities);
  const paymentEntity = useAppSelector(state => state.payment.entity);
  const loading = useAppSelector(state => state.payment.loading);
  const updating = useAppSelector(state => state.payment.updating);
  const updateSuccess = useAppSelector(state => state.payment.updateSuccess);

  const handleClose = () => {
    navigate(`/payment${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    // Cargar Métodos de Pago SIEMPRE (para que el User/Trainer pueda seleccionarlo)
    dispatch(getPaymentMethods({}));

    // Cargar UserData SOLO si es Admin (para que pueda elegir quién registró el pago)
    if (isAdmin) {
      dispatch(getUserData({}));
    }
  }, [isAdmin, isNew]);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    // Si no es nuevo, aseguramos que el ID y AmountPaid sean números
    if (!isNew && values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.amountPaid !== undefined && typeof values.amountPaid !== 'number') {
      values.amountPaid = Number(values.amountPaid);
    }
    values.paymentDate = convertDateTimeToServer(values.paymentDate);

    const entity = {
      ...paymentEntity,
      ...values,
      paymentMethod: paymentMethods.find(it => it.id.toString() === values.paymentMethod?.toString()),

      // Si no es Admin, registeredBy se deja como NULL para que el Backend lo asigne automáticamente.
      registeredBy: isAdmin ? userData.find(it => it.id.toString() === values.registeredBy?.toString()) : null,
    };

    // Si es una nueva creación, quitamos el ID de la entidad para asegurar que se autogenere.
    if (isNew) {
      delete entity.id;
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          paymentDate: displayDefaultDateTime(),
          // El ID se deja vacío/nulo si es nuevo para que se autogenere
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
          <h2 id="gymtrackApp.payment.home.createOrEditLabel" data-cy="PaymentCreateUpdateHeading">
            <Translate contentKey="gymtrackApp.payment.home.createOrEditLabel">Create or edit a Payment</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {/* CAMPO ID: Solo visible y editable al EDITAR (isNew es false) */}
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="payment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              {/* Resto de campos... */}
              <ValidatedField
                label={translate('gymtrackApp.payment.amountPaid')}
                id="payment-amountPaid"
                name="amountPaid"
                data-cy="amountPaid"
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
                data-cy="paymentDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.payment.transactionId')}
                id="payment-transactionId"
                name="transactionId"
                data-cy="transactionId"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.payment.status')}
                id="payment-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              {/* CAMPO MÉTODO DE PAGO: VISIBLE para TODOS */}
              <ValidatedField
                id="payment-paymentMethod"
                name="paymentMethod"
                data-cy="paymentMethod"
                label={translate('gymtrackApp.payment.paymentMethod')}
                type="select"
                required
              >
                <option value="" key="0" />
                {paymentMethods
                  ? paymentMethods.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.methodName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              {/* CAMPO REGISTRADO POR: Solo visible para ADMIN. Si no es Admin, se oculta y el Backend lo asigna automáticamente. */}
              {isAdmin && (
                <>
                  <ValidatedField
                    id="payment-registeredBy"
                    name="registeredBy"
                    data-cy="registeredBy"
                    label={translate('gymtrackApp.payment.registeredBy')}
                    type="select"
                    required
                  >
                    <option value="" key="0" />
                    {userData
                      ? userData.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.document}
                          </option>
                        ))
                      : null}
                  </ValidatedField>
                  <FormText>
                    <Translate contentKey="entity.validation.required">This field is required.</Translate>
                  </FormText>
                </>
              )}
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/payment" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default PaymentUpdate;
