import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import dayjs from 'dayjs';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getGymServices } from 'app/entities/gym-service/gym-service.reducer';
import { getEntities as getSchedules } from 'app/entities/schedule/schedule.reducer';
import { createEntity, getEntity, reset, updateEntity } from './reservation.reducer';

export const ReservationUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const gymServices = useAppSelector(state => state.gymService.entities);
  const schedules = useAppSelector(state => state.schedule.entities);
  const reservationEntity = useAppSelector(state => state.reservation.entity);
  const loading = useAppSelector(state => state.reservation.loading);
  const updating = useAppSelector(state => state.reservation.updating);
  const updateSuccess = useAppSelector(state => state.reservation.updateSuccess);

  const handleClose = () => navigate('/reservation');

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
    dispatch(getGymServices({}));
    dispatch(getSchedules({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) handleClose();
  }, [updateSuccess]);

  const availableSchedules = schedules.filter(sc => sc.availableSlots > 0);

  const saveEntity = values => {
    const selectedSchedule = schedules.find(sc => sc.id.toString() === values.schedule?.toString());
    const entity = {
      ...reservationEntity,
      ...values,
      gymService: gymServices.find(s => s.id.toString() === values.gymService?.toString()),
      schedule: selectedSchedule,
      status: true,
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
          ...reservationEntity,
          gymService: reservationEntity?.gymService?.id,
          schedule: reservationEntity?.schedule?.id,
        };

  return (
    <Row>
      <Col md="8">
        <h2>{isNew ? 'Crear Reserva' : 'Editar Reserva'}</h2>
        {loading ? (
          <p>Cargando...</p>
        ) : (
          <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
            {!isNew && <ValidatedField name="id" readOnly label="ID" id="reservation-id" />}
            <ValidatedField id="reservation-gymService" name="gymService" label="Servicio" type="select" required>
              <option value="" key="0" />
              {gymServices.map(s => (
                <option value={s.id} key={s.id}>
                  {s.serviceName}
                </option>
              ))}
            </ValidatedField>
            <ValidatedField id="reservation-schedule" name="schedule" label="Horario disponible" type="select" required>
              <option value="" key="0" />
              {availableSchedules.map(sc => (
                <option value={sc.id} key={sc.id}>
                  {sc.dayOfWeek} - {dayjs(sc.startTime).format('HH:mm')} a {dayjs(sc.endTime).format('HH:mm')} ({sc.availableSlots} cupos)
                </option>
              ))}
            </ValidatedField>
            <Button tag={Link} to="/reservation" replace color="info">
              <FontAwesomeIcon icon="arrow-left" /> Volver
            </Button>
            &nbsp;
            <Button color="primary" type="submit" disabled={updating}>
              <FontAwesomeIcon icon="save" /> Guardar
            </Button>
          </ValidatedForm>
        )}
      </Col>
    </Row>
  );
};

export default ReservationUpdate;
