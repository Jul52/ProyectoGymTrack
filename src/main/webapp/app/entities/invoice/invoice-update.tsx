import React, { useEffect } from 'react';
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getPayments } from 'app/entities/payment/payment.reducer';
import { getEntities as getPaymentMethods } from 'app/entities/payment-method/payment-method.reducer';
import { getEntities as getUserData } from 'app/entities/user-data/user-data.reducer';
import { getEntities as getGymServices } from 'app/entities/gym-service/gym-service.reducer';
import { createEntity, getEntity, reset, updateEntity } from './invoice.reducer';

export const InvoiceUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();
  const isNew = id === undefined;
  const [searchParams] = useSearchParams();

  // Leemos serviceId y price de la URL si vienen desde gym-service
  const preselectedServiceId = searchParams.get('serviceId');
  const preselectedPrice = searchParams.get('price');

  const payments = useAppSelector(state => state.payment.entities);
  const paymentMethods = useAppSelector(state => state.paymentMethod.entities);
  const userData = useAppSelector(state => state.userData.entities);
  const gymServices = useAppSelector(state => state.gymService.entities);
  const invoiceEntity = useAppSelector(state => state.invoice.entity);
  const loading = useAppSelector(state => state.invoice.loading);
  const updating = useAppSelector(state => state.invoice.updating);
  const updateSuccess = useAppSelector(state => state.invoice.updateSuccess);

  const handleClose = () => {
    navigate(`/invoice${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
    dispatch(getPayments({}));
    dispatch(getPaymentMethods({}));
    dispatch(getUserData({}));
    dispatch(getGymServices({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.total !== undefined && typeof values.total !== 'number') {
      values.total = Number(values.total);
    }
    values.createdDate = convertDateTimeToServer(values.createdDate);

    const entity = {
      ...invoiceEntity,
      ...values,
      payment: payments.find(it => it.id.toString() === values.payment?.toString()),
      paymentMethod: paymentMethods.find(it => it.id.toString() === values.paymentMethod?.toString()),
      userData: userData.find(it => it.id.toString() === values.userData?.toString()),
      service: gymServices.find(it => it.id.toString() === values.service?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () => {
    if (isNew) {
      return {
        createdDate: displayDefaultDateTime(),
        // Prellenamos con los query params si vienen de gym-service
        total: preselectedPrice ?? '',
        service: preselectedServiceId ?? '',
      };
    }
    return {
      ...invoiceEntity,
      createdDate: convertDateTimeFromServer(invoiceEntity.createdDate),
      payment: invoiceEntity?.payment?.id,
      paymentMethod: invoiceEntity?.paymentMethod?.id,
      userData: invoiceEntity?.userData?.id,
      service: invoiceEntity?.service?.id,
    };
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gymtrackApp.invoice.home.createOrEditLabel" data-cy="InvoiceCreateUpdateHeading">
            <Translate contentKey="gymtrackApp.invoice.home.createOrEditLabel">Create or edit a Invoice</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="invoice-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gymtrackApp.invoice.total')}
                id="invoice-total"
                name="total"
                data-cy="total"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.invoice.createdDate')}
                id="invoice-createdDate"
                name="createdDate"
                data-cy="createdDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="invoice-payment"
                name="payment"
                data-cy="payment"
                label={translate('gymtrackApp.invoice.payment')}
                type="select"
              >
                <option value="" key="0" />
                {payments
                  ? payments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="invoice-paymentMethod"
                name="paymentMethod"
                data-cy="paymentMethod"
                label={translate('gymtrackApp.invoice.paymentMethod')}
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
              <ValidatedField
                id="invoice-userData"
                name="userData"
                data-cy="userData"
                label={translate('gymtrackApp.invoice.userData')}
                type="select"
                required
              >
                <option value="" key="0" />
                {userData
                  ? userData.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.documentNumber}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="invoice-service"
                name="service"
                data-cy="service"
                label={translate('gymtrackApp.invoice.service')}
                type="select"
              >
                <option value="" key="0" />
                {gymServices
                  ? gymServices.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.serviceName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/invoice" replace color="info">
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

export default InvoiceUpdate;
