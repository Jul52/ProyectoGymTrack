import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './machine.reducer';

export const MachineDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const machineEntity = useAppSelector(state => state.machine.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="machineDetailsHeading">
          <Translate contentKey="gymtrackApp.machine.detail.title">Machine</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{machineEntity.id}</dd>
          <dt>
            <span id="serial">
              <Translate contentKey="gymtrackApp.machine.serial">Serial</Translate>
            </span>
          </dt>
          <dd>{machineEntity.serial}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="gymtrackApp.machine.description">Description</Translate>
            </span>
          </dt>
          <dd>{machineEntity.description}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="gymtrackApp.machine.status">Status</Translate>
            </span>
          </dt>
          <dd>{machineEntity.status ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.machine.admin">Admin</Translate>
          </dt>
          <dd>{machineEntity.admin ? machineEntity.admin.documentNumber : ''}</dd>
        </dl>
        <Button tag={Link} to="/machine" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/machine/${machineEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MachineDetail;
