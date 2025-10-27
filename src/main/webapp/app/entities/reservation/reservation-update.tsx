import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { getEntities as getGymServices } from 'app/entities/gym-service/gym-service.reducer';
import { getEntities as getUserData } from 'app/entities/user-data/user-data.reducer';
import { createEntity, getEntity, reset, updateEntity } from './reservation.reducer';

export const ReservationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courses = useAppSelector(state => state.course.entities);
  const gymServices = useAppSelector(state => state.gymService.entities);
  const userData = useAppSelector(state => state.userData.entities);
  const reservationEntity = useAppSelector(state => state.reservation.entity);
  const loading = useAppSelector(state => state.reservation.loading);
  const updating = useAppSelector(state => state.reservation.updating);
  const updateSuccess = useAppSelector(state => state.reservation.updateSuccess);

  const handleClose = () => {
    navigate(`/reservation${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCourses({}));
    dispatch(getGymServices({}));
    dispatch(getUserData({}));
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

    const entity = {
      ...reservationEntity,
      ...values,
      course: courses.find(it => it.id.toString() === values.course?.toString()),
      gymService: gymServices.find(it => it.id.toString() === values.gymService?.toString()),
      userData: userData.find(it => it.id.toString() === values.userData?.toString()),
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
          userData: reservationEntity?.userData?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gymtrackApp.reservation.home.createOrEditLabel" data-cy="ReservationCreateUpdateHeading">
            <Translate contentKey="gymtrackApp.reservation.home.createOrEditLabel">Create or edit a Reservation</Translate>
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
                  id="reservation-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gymtrackApp.reservation.status')}
                id="reservation-status"
                name="status"
                data-cy="status"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('gymtrackApp.reservation.description')}
                id="reservation-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.reservation.reservationDate')}
                id="reservation-reservationDate"
                name="reservationDate"
                data-cy="reservationDate"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="reservation-course"
                name="course"
                data-cy="course"
                label={translate('gymtrackApp.reservation.course')}
                type="select"
                required
              >
                <option value="" key="0" />
                {courses
                  ? courses.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.courseName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="reservation-gymService"
                name="gymService"
                data-cy="gymService"
                label={translate('gymtrackApp.reservation.gymService')}
                type="select"
                required
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
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="reservation-userData"
                name="userData"
                data-cy="userData"
                label={translate('gymtrackApp.reservation.userData')}
                type="select"
                required
              >
                <option value="" key="0" />
                {userData
                  ? userData.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.document}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/reservation" replace color="info">
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

export default ReservationUpdate;
