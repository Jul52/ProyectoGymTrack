import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, byteSize, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './machine-incidents.reducer';

export const MachineIncidentsDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const machineIncidentsEntity = useAppSelector(state => state.machineIncidents.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="machineIncidentsDetailsHeading">
          <Translate contentKey="gymtrackApp.machineIncidents.detail.title">MachineIncidents</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{machineIncidentsEntity.id}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="gymtrackApp.machineIncidents.description">Description</Translate>
            </span>
          </dt>
          <dd>{machineIncidentsEntity.description}</dd>
          <dt>
            <span id="image">
              <Translate contentKey="gymtrackApp.machineIncidents.image">Image</Translate>
            </span>
          </dt>
          <dd>
            {machineIncidentsEntity.image ? (
              <div>
                {machineIncidentsEntity.imageContentType ? (
                  <a onClick={openFile(machineIncidentsEntity.imageContentType, machineIncidentsEntity.image)}>
                    <img
                      src={`data:${machineIncidentsEntity.imageContentType};base64,${machineIncidentsEntity.image}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                ) : null}
                <span>
                  {machineIncidentsEntity.imageContentType}, {byteSize(machineIncidentsEntity.image)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="video">
              <Translate contentKey="gymtrackApp.machineIncidents.video">Video</Translate>
            </span>
          </dt>
          <dd>{machineIncidentsEntity.video}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.machineIncidents.incident">Incident</Translate>
          </dt>
          <dd>{machineIncidentsEntity.incident ? machineIncidentsEntity.incident.incidentType : ''}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.machineIncidents.machine">Machine</Translate>
          </dt>
          <dd>{machineIncidentsEntity.machine ? machineIncidentsEntity.machine.description : ''}</dd>
        </dl>
        <Button tag={Link} to="/machine-incidents" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/machine-incidents/${machineIncidentsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MachineIncidentsDetail;
