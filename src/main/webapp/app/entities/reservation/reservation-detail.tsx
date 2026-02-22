import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './reservation.reducer';

export const ReservationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const reservationEntity = useAppSelector(state => state.reservation.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="reservationDetailsHeading">
          <Translate contentKey="gymtrackApp.reservation.detail.title">Reservation</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{reservationEntity.id}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="gymtrackApp.reservation.status">Status</Translate>
            </span>
          </dt>
          <dd>{reservationEntity.status ? 'true' : 'false'}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="gymtrackApp.reservation.description">Description</Translate>
            </span>
          </dt>
          <dd>{reservationEntity.description}</dd>
          <dt>
            <span id="reservationDate">
              <Translate contentKey="gymtrackApp.reservation.reservationDate">Reservation Date</Translate>
            </span>
          </dt>
          <dd>
            {reservationEntity.reservationDate ? (
              <TextFormat value={reservationEntity.reservationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="gymtrackApp.reservation.course">Course</Translate>
          </dt>
          <dd>{reservationEntity.course ? reservationEntity.course.courseName : ''}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.reservation.gymService">Gym Service</Translate>
          </dt>
          <dd>{reservationEntity.gymService ? reservationEntity.gymService.serviceName : ''}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.reservation.userData">User Data</Translate>
          </dt>
          <dd>{reservationEntity.userData ? reservationEntity.userData.document : ''}</dd>
        </dl>
        <Button tag={Link} to="/reservation" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/reservation/${reservationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ReservationDetail;
