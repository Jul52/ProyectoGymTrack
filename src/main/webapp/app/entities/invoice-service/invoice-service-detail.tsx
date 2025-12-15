import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './invoice-service.reducer';

export const InvoiceServiceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const invoiceServiceEntity = useAppSelector(state => state.invoiceService.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="invoiceServiceDetailsHeading">
          <Translate contentKey="gymtrackApp.invoiceService.detail.title">InvoiceService</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{invoiceServiceEntity.id}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="gymtrackApp.invoiceService.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{invoiceServiceEntity.quantity}</dd>
          <dt>
            <span id="subtotal">
              <Translate contentKey="gymtrackApp.invoiceService.subtotal">Subtotal</Translate>
            </span>
          </dt>
          <dd>{invoiceServiceEntity.subtotal}</dd>
          <dt>
            <span id="salePrice">
              <Translate contentKey="gymtrackApp.invoiceService.salePrice">Sale Price</Translate>
            </span>
          </dt>
          <dd>{invoiceServiceEntity.salePrice}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.invoiceService.invoice">Invoice</Translate>
          </dt>
          <dd>{invoiceServiceEntity.invoice ? invoiceServiceEntity.invoice.id : ''}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.invoiceService.service">Service</Translate>
          </dt>
          <dd>{invoiceServiceEntity.service ? invoiceServiceEntity.service.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/invoice-service" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/invoice-service/${invoiceServiceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InvoiceServiceDetail;
