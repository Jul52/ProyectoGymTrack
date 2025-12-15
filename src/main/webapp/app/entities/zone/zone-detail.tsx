import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './zone.reducer';

export const ZoneDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const zoneEntity = useAppSelector(state => state.zone.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="zoneDetailsHeading">
          <Translate contentKey="gymtrackApp.zone.detail.title">Zone</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{zoneEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="gymtrackApp.zone.name">Name</Translate>
            </span>
          </dt>
          <dd>{zoneEntity.name}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="gymtrackApp.zone.status">Status</Translate>
            </span>
          </dt>
          <dd>{zoneEntity.status ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.zone.course">Course</Translate>
          </dt>
          <dd>
            {zoneEntity.courses
              ? zoneEntity.courses.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.courseName}</a>
                    {zoneEntity.courses && i === zoneEntity.courses.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/zone" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/zone/${zoneEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ZoneDetail;
