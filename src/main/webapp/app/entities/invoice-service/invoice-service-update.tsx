import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getInvoices } from 'app/entities/invoice/invoice.reducer';
import { getEntities as getGymServices } from 'app/entities/gym-service/gym-service.reducer';
import { createEntity, getEntity, reset, updateEntity } from './invoice-service.reducer';

export const InvoiceServiceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const invoices = useAppSelector(state => state.invoice.entities);
  const gymServices = useAppSelector(state => state.gymService.entities);
  const invoiceServiceEntity = useAppSelector(state => state.invoiceService.entity);
  const loading = useAppSelector(state => state.invoiceService.loading);
  const updating = useAppSelector(state => state.invoiceService.updating);
  const updateSuccess = useAppSelector(state => state.invoiceService.updateSuccess);

  const handleClose = () => {
    navigate(`/invoice-service${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getInvoices({}));
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
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }
    if (values.subtotal !== undefined && typeof values.subtotal !== 'number') {
      values.subtotal = Number(values.subtotal);
    }
    if (values.salePrice !== undefined && typeof values.salePrice !== 'number') {
      values.salePrice = Number(values.salePrice);
    }

    const entity = {
      ...invoiceServiceEntity,
      ...values,
      invoice: invoices.find(it => it.id.toString() === values.invoice?.toString()),
      service: gymServices.find(it => it.id.toString() === values.service?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...invoiceServiceEntity,
          invoice: invoiceServiceEntity?.invoice?.id,
          service: invoiceServiceEntity?.service?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gymtrackApp.invoiceService.home.createOrEditLabel" data-cy="InvoiceServiceCreateUpdateHeading">
            <Translate contentKey="gymtrackApp.invoiceService.home.createOrEditLabel">Create or edit a InvoiceService</Translate>
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
                  id="invoice-service-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gymtrackApp.invoiceService.quantity')}
                id="invoice-service-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.invoiceService.subtotal')}
                id="invoice-service-subtotal"
                name="subtotal"
                data-cy="subtotal"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.invoiceService.salePrice')}
                id="invoice-service-salePrice"
                name="salePrice"
                data-cy="salePrice"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="invoice-service-invoice"
                name="invoice"
                data-cy="invoice"
                label={translate('gymtrackApp.invoiceService.invoice')}
                type="select"
                required
              >
                <option value="" key="0" />
                {invoices
                  ? invoices.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="invoice-service-service"
                name="service"
                data-cy="service"
                label={translate('gymtrackApp.invoiceService.service')}
                type="select"
                required
              >
                <option value="" key="0" />
                {gymServices
                  ? gymServices.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/invoice-service" replace color="info">
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

export default InvoiceServiceUpdate;
