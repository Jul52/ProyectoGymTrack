import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './payment.reducer';

export const PaymentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const paymentEntity = useAppSelector(state => state.payment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="paymentDetailsHeading">
          <Translate contentKey="gymtrackApp.payment.detail.title">Payment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.id}</dd>
          <dt>
            <span id="amountPaid">
              <Translate contentKey="gymtrackApp.payment.amountPaid">Amount Paid</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.amountPaid}</dd>
          <dt>
            <span id="paymentDate">
              <Translate contentKey="gymtrackApp.payment.paymentDate">Payment Date</Translate>
            </span>
          </dt>
          <dd>
            {paymentEntity.paymentDate ? <TextFormat value={paymentEntity.paymentDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="transactionId">
              <Translate contentKey="gymtrackApp.payment.transactionId">Transaction Id</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.transactionId}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="gymtrackApp.payment.status">Status</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.status}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.payment.paymentMethod">Payment Method</Translate>
          </dt>
          <dd>{paymentEntity.paymentMethod ? paymentEntity.paymentMethod.methodName : ''}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.payment.registeredBy">Registered By</Translate>
          </dt>
          <dd>{paymentEntity.registeredBy ? paymentEntity.registeredBy.document : ''}</dd>
        </dl>
        <Button tag={Link} to="/payment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/payment/${paymentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PaymentDetail;
