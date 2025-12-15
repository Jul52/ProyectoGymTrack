import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedBlobField, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getIncidents } from 'app/entities/incident/incident.reducer';
import { getEntities as getMachines } from 'app/entities/machine/machine.reducer';
import { createEntity, getEntity, reset, updateEntity } from './machine-incidents.reducer';

export const MachineIncidentsUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const incidents = useAppSelector(state => state.incident.entities);
  const machines = useAppSelector(state => state.machine.entities);
  const machineIncidentsEntity = useAppSelector(state => state.machineIncidents.entity);
  const loading = useAppSelector(state => state.machineIncidents.loading);
  const updating = useAppSelector(state => state.machineIncidents.updating);
  const updateSuccess = useAppSelector(state => state.machineIncidents.updateSuccess);

  const handleClose = () => {
    navigate(`/machine-incidents${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getIncidents({}));
    dispatch(getMachines({}));
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
      ...machineIncidentsEntity,
      ...values,
      incident: incidents.find(it => it.id.toString() === values.incident?.toString()),
      machine: machines.find(it => it.id.toString() === values.machine?.toString()),
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
          ...machineIncidentsEntity,
          incident: machineIncidentsEntity?.incident?.id,
          machine: machineIncidentsEntity?.machine?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gymtrackApp.machineIncidents.home.createOrEditLabel" data-cy="MachineIncidentsCreateUpdateHeading">
            <Translate contentKey="gymtrackApp.machineIncidents.home.createOrEditLabel">Create or edit a MachineIncidents</Translate>
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
                  id="machine-incidents-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gymtrackApp.machineIncidents.description')}
                id="machine-incidents-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedBlobField
                label={translate('gymtrackApp.machineIncidents.image')}
                id="machine-incidents-image"
                name="image"
                data-cy="image"
                isImage
                accept="image/*"
              />
              <ValidatedField
                label={translate('gymtrackApp.machineIncidents.video')}
                id="machine-incidents-video"
                name="video"
                data-cy="video"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                id="machine-incidents-incident"
                name="incident"
                data-cy="incident"
                label={translate('gymtrackApp.machineIncidents.incident')}
                type="select"
                required
              >
                <option value="" key="0" />
                {incidents
                  ? incidents.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.incidentType}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="machine-incidents-machine"
                name="machine"
                data-cy="machine"
                label={translate('gymtrackApp.machineIncidents.machine')}
                type="select"
                required
              >
                <option value="" key="0" />
                {machines
                  ? machines.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.description}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/machine-incidents" replace color="info">
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

export default MachineIncidentsUpdate;
