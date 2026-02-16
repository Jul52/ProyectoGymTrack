import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { getEntities as getGymServices } from 'app/entities/gym-service/gym-service.reducer';
import { getEntities as getUserData } from 'app/entities/user-data/user-data.reducer';
import { getEntities as getSchedules } from 'app/entities/schedule/schedule.reducer';

import { createEntity, getEntity, reset, updateEntity } from './reservation.reducer';

export const ReservationUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courses = useAppSelector(state => state.course.entities);
  const gymServices = useAppSelector(state => state.gymService.entities);
  const users = useAppSelector(state => state.userData.entities);
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

    dispatch(getCourses({}));
    dispatch(getGymServices({}));
    dispatch(getUserData({}));
    dispatch(getSchedules({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...reservationEntity,
      ...values,
      course: courses.find(c => c.id.toString() === values.course?.toString()),
      gymService: gymServices.find(s => s.id.toString() === values.gymService?.toString()),
      registeredBy: users.find(u => u.id.toString() === values.registeredBy?.toString()),
      schedule: schedules.find(sc => sc.id.toString() === values.schedule?.toString()),
      status: values.status,
      description: values.description,
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
          course: reservationEntity?.course?.id,
          gymService: reservationEntity?.gymService?.id,
          registeredBy: reservationEntity?.registeredBy?.id,
          schedule: reservationEntity?.schedule?.id,
          description: '',
          status: reservationEntity?.status,
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
            {/* Curso */}
            <ValidatedField id="reservation-course" name="course" label="Curso" type="select" required>
              <option value="" key="0" />
              {courses.map(c => (
                <option value={c.id} key={c.id}>
                  {c.courseName}
                </option>
              ))}
            </ValidatedField>
            {/* Servicio */}
            <ValidatedField id="reservation-gymService" name="gymService" label="Servicio" type="select" required>
              <option value="" key="0" />
              {gymServices.map(s => (
                <option value={s.id} key={s.id}>
                  {s.serviceName}
                </option>
              ))}
            </ValidatedField>
            {/* Datos de usuario */}
            <ValidatedField id="reservation-registeredBy" name="registeredBy" label="Usuario" type="select" required>
              <option value="" key="0" />
              {users.map(u => (
                <option value={u.id} key={u.id}>
                  {u.fullName} ({u.document})
                </option>
              ))}
            </ValidatedField>
            {/* Horario */}
            <ValidatedField id="reservation-schedule" name="schedule" label="Horario" type="select" required>
              <option value="" key="0" />
              {schedules.map(sc => (
                <option value={sc.id} key={sc.id}>
                  {sc.day} - {sc.startTime} a {sc.endTime}
                </option>
              ))}
            </ValidatedField>
            {/* Descripción */}
            <ValidatedField id="reservation-description" name="description" label="Descripción" type="text" maxLength={255} />
            {/* Estado */}
            <ValidatedField id="reservation-status" name="status" label="Estado" type="checkbox" />
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
