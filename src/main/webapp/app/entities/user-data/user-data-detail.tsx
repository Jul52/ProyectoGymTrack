import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-data.reducer';

export const UserDataDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userDataEntity = useAppSelector(state => state.userData.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userDataDetailsHeading">
          <Translate contentKey="gymtrackApp.userData.detail.title">UserData</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.id}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="gymtrackApp.userData.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.firstName}</dd>
          <dt>
            <span id="secondName">
              <Translate contentKey="gymtrackApp.userData.secondName">Second Name</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.secondName}</dd>
          <dt>
            <span id="firstLastName">
              <Translate contentKey="gymtrackApp.userData.firstLastName">First Last Name</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.firstLastName}</dd>
          <dt>
            <span id="secondLastName">
              <Translate contentKey="gymtrackApp.userData.secondLastName">Second Last Name</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.secondLastName}</dd>
          <dt>
            <span id="document">
              <Translate contentKey="gymtrackApp.userData.document">Document</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.document}</dd>
          <dt>
            <span id="phoneNumber">
              <Translate contentKey="gymtrackApp.userData.phoneNumber">Phone Number</Translate>
            </span>
          </dt>
          <dd>{userDataEntity.phoneNumber}</dd>
          <dt>
            <span id="birthDate">
              <Translate contentKey="gymtrackApp.userData.birthDate">Birth Date</Translate>
            </span>
          </dt>
          <dd>
            {userDataEntity.birthDate ? <TextFormat value={userDataEntity.birthDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="gymtrackApp.userData.user">User</Translate>
          </dt>
          <dd>{userDataEntity.user ? userDataEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="gymtrackApp.userData.documentType">Document Type</Translate>
          </dt>
          <dd>{userDataEntity.documentType ? userDataEntity.documentType.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/user-data" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-data/${userDataEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserDataDetail;
