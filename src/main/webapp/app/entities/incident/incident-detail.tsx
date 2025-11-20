import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './incident.reducer';

export const IncidentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const incidentEntity = useAppSelector(state => state.incident.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="incidentDetailsHeading">
          <Translate contentKey="gymtrackApp.incident.detail.title">Incident</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{incidentEntity.id}</dd>
          <dt>
            <span id="incidentType">
              <Translate contentKey="gymtrackApp.incident.incidentType">Incident Type</Translate>
            </span>
          </dt>
          <dd>{incidentEntity.incidentType}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="gymtrackApp.incident.description">Description</Translate>
            </span>
          </dt>
          <dd>{incidentEntity.description}</dd>
          <dt>
            <span id="reportedDate">
              <Translate contentKey="gymtrackApp.incident.reportedDate">Reported Date</Translate>
            </span>
          </dt>
          <dd>
            {incidentEntity.reportedDate ? <TextFormat value={incidentEntity.reportedDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/incident" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/incident/${incidentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default IncidentDetail;
