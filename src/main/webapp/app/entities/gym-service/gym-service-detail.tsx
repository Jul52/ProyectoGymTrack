import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './gym-service.reducer';

export const GymServiceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const gymServiceEntity = useAppSelector(state => state.gymService.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="gymServiceDetailsHeading">
          <Translate contentKey="gymtrackApp.gymService.detail.title">GymService</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{gymServiceEntity.id}</dd>
          <dt>
            <span id="serviceName">
              <Translate contentKey="gymtrackApp.gymService.serviceName">Service Name</Translate>
            </span>
          </dt>
          <dd>{gymServiceEntity.serviceName}</dd>
          <dt>
            <span id="serviceDescription">
              <Translate contentKey="gymtrackApp.gymService.serviceDescription">Service Description</Translate>
            </span>
          </dt>
          <dd>{gymServiceEntity.serviceDescription}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="gymtrackApp.gymService.price">Price</Translate>
            </span>
          </dt>
          <dd>{gymServiceEntity.price}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="gymtrackApp.gymService.status">Status</Translate>
            </span>
          </dt>
          <dd>{gymServiceEntity.status ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.gymService.category">Category</Translate>
          </dt>
          <dd>{gymServiceEntity.category ? gymServiceEntity.category.categoryName : ''}</dd>
        </dl>
        <Button tag={Link} to="/gym-service" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/gym-service/${gymServiceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default GymServiceDetail;
