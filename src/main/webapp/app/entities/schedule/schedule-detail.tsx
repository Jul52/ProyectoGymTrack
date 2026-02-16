import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './schedule.reducer';

export const ScheduleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const scheduleEntity = useAppSelector(state => state.schedule.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="scheduleDetailsHeading">
          <Translate contentKey="gymtrackApp.schedule.detail.title">Schedule</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.id}</dd>
          <dt>
            <span id="dayOfWeek">
              <Translate contentKey="gymtrackApp.schedule.dayOfWeek">Day Of Week</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.dayOfWeek}</dd>
          <dt>
            <span id="startTime">
              <Translate contentKey="gymtrackApp.schedule.startTime">Start Time</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.startTime ? <TextFormat value={scheduleEntity.startTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="endTime">
              <Translate contentKey="gymtrackApp.schedule.endTime">End Time</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.endTime ? <TextFormat value={scheduleEntity.endTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.schedule.course">Course</Translate>
          </dt>
          <dd>{scheduleEntity.course ? scheduleEntity.course.courseName : ''}</dd>
          <dt>
            <span id="availableSlots">
              <Translate contentKey="gymtrackApp.schedule.availableSlots">Available Slots</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.availableSlots}</dd>
        </dl>
        <Button tag={Link} to="/schedule" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/schedule/${scheduleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ScheduleDetail;
