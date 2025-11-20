import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './invoice.reducer';

export const InvoiceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const invoiceEntity = useAppSelector(state => state.invoice.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="invoiceDetailsHeading">
          <Translate contentKey="gymtrackApp.invoice.detail.title">Invoice</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{invoiceEntity.id}</dd>
          <dt>
            <span id="total">
              <Translate contentKey="gymtrackApp.invoice.total">Total</Translate>
            </span>
          </dt>
          <dd>{invoiceEntity.total}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="gymtrackApp.invoice.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {invoiceEntity.createdDate ? <TextFormat value={invoiceEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="gymtrackApp.invoice.payment">Payment</Translate>
          </dt>
          <dd>{invoiceEntity.payment ? invoiceEntity.payment.id : ''}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.invoice.paymentMethod">Payment Method</Translate>
          </dt>
          <dd>{invoiceEntity.paymentMethod ? invoiceEntity.paymentMethod.methodName : ''}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.invoice.userData">User Data</Translate>
          </dt>
          <dd>{invoiceEntity.userData ? invoiceEntity.userData.document : ''}</dd>
        </dl>
        <Button tag={Link} to="/invoice" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/invoice/${invoiceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InvoiceDetail;
