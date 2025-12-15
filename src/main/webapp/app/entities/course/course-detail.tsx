import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './course.reducer';

export const CourseDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const courseEntity = useAppSelector(state => state.course.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseDetailsHeading">
          <Translate contentKey="gymtrackApp.course.detail.title">Course</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{courseEntity.id}</dd>
          <dt>
            <span id="courseName">
              <Translate contentKey="gymtrackApp.course.courseName">Course Name</Translate>
            </span>
          </dt>
          <dd>{courseEntity.courseName}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="gymtrackApp.course.status">Status</Translate>
            </span>
          </dt>
          <dd>{courseEntity.status ? 'true' : 'false'}</dd>
          <dt>
            <span id="startDate">
              <Translate contentKey="gymtrackApp.course.startDate">Start Date</Translate>
            </span>
          </dt>
          <dd>
            {courseEntity.startDate ? <TextFormat value={courseEntity.startDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="endDate">
              <Translate contentKey="gymtrackApp.course.endDate">End Date</Translate>
            </span>
          </dt>
          <dd>{courseEntity.endDate ? <TextFormat value={courseEntity.endDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="capacity">
              <Translate contentKey="gymtrackApp.course.capacity">Capacity</Translate>
            </span>
          </dt>
          <dd>{courseEntity.capacity}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.course.zone">Zone</Translate>
          </dt>
          <dd>
            {courseEntity.zones
              ? courseEntity.zones.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {courseEntity.zones && i === courseEntity.zones.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>
            <Translate contentKey="gymtrackApp.course.trainer">Trainer</Translate>
          </dt>
          <dd>{courseEntity.trainer ? courseEntity.trainer.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/course" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/course/${courseEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseDetail;
