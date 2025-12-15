import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Button, Row, Col, Card, CardBody, CardTitle, CardText } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye, faPencilAlt, faTrash, faPlus, faSync } from '@fortawesome/free-solid-svg-icons';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from './category.reducer';

export const Category = () => {
  const dispatch = useAppDispatch();
  const handleSyncList = () => {
    dispatch(getEntities({ page: 0, size: 20, sort: 'id,asc' }));
  };

  const categoryList = useAppSelector(state => state.category.entities);
  const loading = useAppSelector(state => state.category.loading);

  useEffect(() => {
    handleSyncList();
  }, []);

  return (
    <div className="category-page-container">
      <h2 id="category-heading" data-cy="CategoryHeading" className="text-dark">
        {' '}
        {/* Texto oscuro */}
        <Translate contentKey="gymtrackApp.category.home.title">Categorías</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="primary" onClick={handleSyncList} disabled={loading}>
            {' '}
            {/* Botón azul */}
            <FontAwesomeIcon icon={faSync} spin={loading} />{' '}
            <Translate contentKey="gymtrackApp.category.home.refreshListLabel">Refrescar Lista</Translate>
          </Button>
          <Link to="/category/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon={faPlus} />
            &nbsp;
            <Translate contentKey="gymtrackApp.category.home.createLabel">Crear nueva Categoría</Translate>
          </Link>
        </div>
      </h2>

      {loading && <div className="loading-indicator">Cargando...</div>}

      {categoryList && categoryList.length > 0 ? (
        // INICIO DE LA VISTA EN TARJETAS (GRID) - Diseño Blanco/Minimalista
        <div className="category-grid-container mt-4">
          <Row>
            {categoryList.map((category, i) => (
              <Col md="4" key={`entity-${i}`} className="mb-4">
                <Card className="category-card h-100 bg-white shadow-sm border-0">
                  {' '}
                  {/* Fondo blanco, sin borde Bootstrap */}
                  <CardBody>
                    <CardTitle tag="h5" className="text-dark">
                      {' '}
                      {/* Título oscuro */}
                      {category.categoryName}
                    </CardTitle>
                    <CardText className="text-secondary">
                      {' '}
                      {/* Texto secundario para el ID */}
                      <strong>ID:</strong> {category.id}
                    </CardText>

                    {/* Botones de Acción - Todos en Azul (Primary) */}
                    <div className="btn-group flex-btn-group-container mt-3">
                      <Button
                        tag={Link}
                        to={`/category/${category.id}`}
                        color="primary" // Botón azul
                        size="sm"
                        data-cy="entityDetailsButton"
                        className="me-2"
                        outline // Botón con borde (similar al mockup)
                      >
                        <FontAwesomeIcon icon={faEye} /> <span className="d-none d-md-inline">Ver</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/category/${category.id}/edit`}
                        color="primary" // Botón azul
                        size="sm"
                        data-cy="entityEditButton"
                        className="me-2"
                        outline // Botón con borde (similar al mockup)
                      >
                        <FontAwesomeIcon icon={faPencilAlt} /> <span className="d-none d-md-inline">Editar</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/category/${category.id}/delete`}
                        color="danger" // El botón de eliminar se mantiene rojo para seguridad visual
                        size="sm"
                        data-cy="entityDeleteButton"
                        outline
                      >
                        <FontAwesomeIcon icon={faTrash} /> <span className="d-none d-md-inline">Eliminar</span>
                      </Button>
                    </div>
                  </CardBody>
                </Card>
              </Col>
            ))}
          </Row>
        </div>
      ) : (
        !loading && (
          <div className="alert alert-warning">
            <Translate contentKey="gymtrackApp.category.home.notFound">No se encontraron Categorías</Translate>
          </div>
        )
      )}
    </div>
  );
};

export default Category;
