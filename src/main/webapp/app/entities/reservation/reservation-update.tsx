import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Card, CardBody, Col, Container, Row } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import dayjs from 'dayjs';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { createEntity, getEntity, reset, updateEntity } from './reservation.reducer';

export const ReservationUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const reservationEntity = useAppSelector(state => state.reservation.entity);
  const loading = useAppSelector(state => state.reservation.loading);
  const updating = useAppSelector(state => state.reservation.updating);
  const updateSuccess = useAppSelector(state => state.reservation.updateSuccess);

  const [myServices, setMyServices] = useState([]);
  const [availableSchedules, setAvailableSchedules] = useState([]);
  const [selectedServiceId, setSelectedServiceId] = useState<number | null>(null);
  const [selectedScheduleId, setSelectedScheduleId] = useState<number | null>(null);

  const handleClose = () => navigate('/reservation');

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
    axios.get('/api/reservations/my-services').then(res => setMyServices(res.data));
  }, []);

  useEffect(() => {
    if (updateSuccess) handleClose();
  }, [updateSuccess]);

  useEffect(() => {
    if (selectedServiceId) {
      axios.get(`/api/reservations/available-schedules/${selectedServiceId}`).then(res => setAvailableSchedules(res.data));
    } else {
      setAvailableSchedules([]);
      setSelectedScheduleId(null);
    }
  }, [selectedServiceId]);

  const handleServiceChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedServiceId(Number(e.target.value) || null);
    setSelectedScheduleId(null);
  };

  const handleScheduleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedScheduleId(Number(e.target.value) || null);
  };

  const saveEntity = () => {
    const selectedSchedule = availableSchedules.find((sc: any) => sc.id === selectedScheduleId);
    const selectedService = myServices.find((s: any) => s.id === selectedServiceId);
    if (!selectedService || !selectedSchedule) return;
    const entity = {
      ...reservationEntity,
      gymService: selectedService,
      schedule: selectedSchedule,
      status: true,
    };
    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  return (
    <Container className="mt-4 mb-5">
      <Row className="justify-content-center">
        <Col md="7">
          <Card className="shadow-sm">
            <CardBody className="p-4">
              <h3 className="mb-4 text-center">{isNew ? 'Nueva Reserva' : 'Editar Reserva'}</h3>

              {loading ? (
                <p className="text-center">Cargando...</p>
              ) : (
                <div>
                  {!isNew && (
                    <div className="mb-3">
                      <label className="form-label fw-bold">ID</label>
                      <input className="form-control" readOnly value={reservationEntity?.id ?? ''} />
                    </div>
                  )}

                  <div className="mb-4">
                    <label htmlFor="reservation-gymService" className="form-label fw-bold">
                      Selecciona tu servicio <span className="text-danger">*</span>
                    </label>
                    <select
                      id="reservation-gymService"
                      className="form-select"
                      value={selectedServiceId ?? ''}
                      onChange={handleServiceChange}
                      required
                    >
                      <option value="">-- Elige un servicio --</option>
                      {myServices.map((s: any) => (
                        <option value={s.id} key={s.id}>
                          {s.serviceName}
                        </option>
                      ))}
                    </select>
                    {myServices.length === 0 && <small className="text-muted">No tienes servicios contratados actualmente.</small>}
                  </div>

                  <div className="mb-4">
                    <label htmlFor="reservation-schedule" className="form-label fw-bold">
                      Horario disponible <span className="text-danger">*</span>
                    </label>
                    <select
                      id="reservation-schedule"
                      className="form-select"
                      value={selectedScheduleId ?? ''}
                      onChange={handleScheduleChange}
                      disabled={!selectedServiceId}
                      required
                    >
                      <option value="">-- Elige un horario --</option>
                      {availableSchedules.map((sc: any) => (
                        <option value={sc.id} key={sc.id}>
                          {sc.course?.courseName} — {sc.dayOfWeek} {dayjs(sc.startTime).format('HH:mm')} a{' '}
                          {dayjs(sc.endTime).format('HH:mm')} ({sc.availableSlots} cupos disponibles)
                        </option>
                      ))}
                    </select>
                    {selectedServiceId && availableSchedules.length === 0 && (
                      <small className="text-warning">No hay horarios disponibles para este servicio.</small>
                    )}
                  </div>

                  <div className="d-flex justify-content-between mt-4 gap-3">
                    <Button tag={Link} to="/reservation" color="secondary" className="px-4">
                      <FontAwesomeIcon icon="arrow-left" /> Volver
                    </Button>
                    <Button
                      color="primary"
                      onClick={saveEntity}
                      disabled={updating || !selectedServiceId || !selectedScheduleId}
                      className="px-4"
                    >
                      <FontAwesomeIcon icon="save" /> Guardar reserva
                    </Button>
                  </div>
                </div>
              )}
            </CardBody>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default ReservationUpdate;
