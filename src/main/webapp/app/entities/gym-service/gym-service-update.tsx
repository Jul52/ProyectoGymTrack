import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCategories } from 'app/entities/category/category.reducer';
import { createEntity, getEntity, reset, updateEntity } from './gym-service.reducer';

export const GymServiceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const categories = useAppSelector(state => state.category.entities);
  const gymServiceEntity = useAppSelector(state => state.gymService.entity);
  const loading = useAppSelector(state => state.gymService.loading);
  const updating = useAppSelector(state => state.gymService.updating);
  const updateSuccess = useAppSelector(state => state.gymService.updateSuccess);
  const [accessType, setAccessType] = React.useState('NONE');

  const handleClose = () => {
    navigate(`/gym-service${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCategories({}));
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
    if (values.price !== undefined && typeof values.price !== 'number') {
      values.price = Number(values.price);
    }
    if (values.maxClassesPerMonth !== undefined && typeof values.maxClassesPerMonth !== 'number') {
      values.maxClassesPerMonth = Number(values.maxClassesPerMonth);
    }

    const entity = {
      ...gymServiceEntity,
      ...values,
      category: categories.find(it => it.id.toString() === values.category?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          courseAccessType: 'NONE',
        }
      : {
          ...gymServiceEntity,
          category: gymServiceEntity?.category?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gymtrackApp.gymService.home.createOrEditLabel" data-cy="GymServiceCreateUpdateHeading">
            <Translate contentKey="gymtrackApp.gymService.home.createOrEditLabel">Create or edit a GymService</Translate>
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
                  id="gym-service-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gymtrackApp.gymService.serviceName')}
                id="gym-service-serviceName"
                name="serviceName"
                data-cy="serviceName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.gymService.serviceDescription')}
                id="gym-service-serviceDescription"
                name="serviceDescription"
                data-cy="serviceDescription"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.gymService.price')}
                id="gym-service-price"
                name="price"
                data-cy="price"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('gymtrackApp.gymService.status')}
                id="gym-service-status"
                name="status"
                data-cy="status"
                check
                type="checkbox"
              />
              <ValidatedField
                label="Tipo de acceso a cursos"
                id="gym-service-courseAccessType"
                name="courseAccessType"
                data-cy="courseAccessType"
                type="select"
                onChange={e => setAccessType(e.target.value)}
              >
                <option value="NONE">Sin acceso a cursos</option>
                <option value="LIMITED">Acceso limitado</option>
                <option value="UNLIMITED">Acceso ilimitado</option>
              </ValidatedField>
              {accessType === 'LIMITED' && (
                <ValidatedField
                  label="Máximo de clases por mes"
                  id="gym-service-maxClassesPerMonth"
                  name="maxClassesPerMonth"
                  data-cy="maxClassesPerMonth"
                  type="number"
                  validate={{
                    required: { value: true, message: 'Este campo es obligatorio cuando el acceso es limitado' },
                    min: { value: 1, message: 'Debe ser al menos 1 clase' },
                  }}
                />
              )}
              <ValidatedField
                id="gym-service-category"
                name="category"
                data-cy="category"
                label={translate('gymtrackApp.gymService.category')}
                type="select"
                required
              >
                <option value="" key="0" />
                {categories
                  ? categories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.categoryName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/gym-service" replace color="info">
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

export default GymServiceUpdate;
