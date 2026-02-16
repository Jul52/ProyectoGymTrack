import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity } from './reservation.reducer';

export const ReservationDetail = () => {
  const dispatch = useAppDispatch();
  const { id } = useParams<'id'>();

  const reservationEntity = useAppSelector(state => state.reservation.entity);

  useEffect(() => {
    dispatch(getEntity(id));
  }, [id]);

  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="gymtrackApp.reservation.detail.title">Reservation</Translate> [<b>{reservationEntity.id}</b>]
        </h2>
        <dl className="row">
          <dt className="col-sm-3">
            <Translate contentKey="gymtrackApp.reservation.course">Course</Translate>
          </dt>
          <dd className="col-sm-9">{reservationEntity.course?.courseName}</dd>

          <dt className="col-sm-3">
            <Translate contentKey="gymtrackApp.reservation.gymService">Gym Service</Translate>
          </dt>
          <dd className="col-sm-9">{reservationEntity.gymService?.serviceName}</dd>

          <dt className="col-sm-3">
            <Translate contentKey="gymtrackApp.reservation.registeredBy">Usuario</Translate>
          </dt>
          <dd className="col-sm-9">{reservationEntity.registeredBy?.fullName}</dd>

          <dt className="col-sm-3">
            <Translate contentKey="gymtrackApp.reservation.schedule">Horario</Translate>
          </dt>
          <dd className="col-sm-9">
            {reservationEntity.schedule?.day} - {reservationEntity.schedule?.startTime}
          </dd>

          <dt className="col-sm-3">
            <Translate contentKey="gymtrackApp.reservation.status">Status</Translate>
          </dt>
          <dd className="col-sm-9">{reservationEntity.status ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/reservation" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> Volver
        </Button>
        &nbsp;
      </Col>
    </Row>
  );
};

export default ReservationDetail;
