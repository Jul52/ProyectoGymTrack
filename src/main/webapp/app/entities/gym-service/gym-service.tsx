import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import axios from 'axios';

import { getEntities } from './gym-service.reducer';

export const GymService = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const [myServiceIds, setMyServiceIds] = useState<number[]>([]);

  const gymServiceList = useAppSelector(state => state.gymService.entities);
  const loading = useAppSelector(state => state.gymService.loading);
  const totalItems = useAppSelector(state => state.gymService.totalItems);
  const isAdmin = useAppSelector(state => state.authentication.account.authorities?.includes(AUTHORITIES.ADMIN));

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) navigate(`${pageLocation.pathname}${endURL}`);
  };

  useEffect(() => {
    sortEntities();
    // Cargar servicios que ya compró el usuario
    axios
      .get('/api/reservations/my-services')
      .then(res => setMyServiceIds(res.data.map((s: any) => s.id)))
      .catch(() => {});
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({ ...paginationState, activePage: +page, sort: sortSplit[0], order: sortSplit[1] });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({ ...paginationState, order: paginationState.order === ASC ? DESC : ASC, sort: p });
  };

  const handlePagination = currentPage => setPaginationState({ ...paginationState, activePage: currentPage });
  const handleSyncList = () => sortEntities();

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) return faSort;
    return order === ASC ? faSortUp : faSortDown;
  };

  const handleBuy = (gymService: any) => {
    navigate(`/payment/new?serviceId=${gymService.id}&price=${gymService.price}&serviceName=${encodeURIComponent(gymService.serviceName)}`);
  };

  return (
    <div>
      <h2 id="gym-service-heading" data-cy="GymServiceHeading">
        <Translate contentKey="gymtrackApp.gymService.home.title">Gym Services</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gymtrackApp.gymService.home.refreshListLabel">Refresh List</Translate>
          </Button>
          {isAdmin && (
            <Link to="/gym-service/new" className="btn btn-primary jh-create-entity">
              <FontAwesomeIcon icon="plus" />
              &nbsp;
              <Translate contentKey="gymtrackApp.gymService.home.createLabel">Create new Gym Service</Translate>
            </Link>
          )}
        </div>
      </h2>

      <div className="table-responsive">
        {gymServiceList && gymServiceList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="gymtrackApp.gymService.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('serviceName')}>
                  <Translate contentKey="gymtrackApp.gymService.serviceName">Service Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('serviceName')} />
                </th>
                <th className="hand" onClick={sort('serviceDescription')}>
                  <Translate contentKey="gymtrackApp.gymService.serviceDescription">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('serviceDescription')} />
                </th>
                <th className="hand" onClick={sort('price')}>
                  <Translate contentKey="gymtrackApp.gymService.price">Price</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('price')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="gymtrackApp.gymService.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th>
                  <Translate contentKey="gymtrackApp.gymService.category">Category</Translate>
                </th>
                {!isAdmin && <th>Acción</th>}
                {isAdmin && <th />}
              </tr>
            </thead>
            <tbody>
              {gymServiceList.map((gymService, i) => {
                const alreadyBought = myServiceIds.includes(gymService.id);
                return (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`/gym-service/${gymService.id}`} color="link" size="sm">
                        {gymService.id}
                      </Button>
                    </td>
                    <td>{gymService.serviceName}</td>
                    <td>{gymService.serviceDescription}</td>
                    <td>${Number(gymService.price).toLocaleString('es-CO')}</td>
                    <td>
                      <span
                        style={{
                          background: gymService.status ? '#dcfce7' : '#fee2e2',
                          color: gymService.status ? '#15803d' : '#dc2626',
                          padding: '3px 10px',
                          borderRadius: '999px',
                          fontSize: '12px',
                          fontWeight: 600,
                        }}
                      >
                        {gymService.status ? '✅ Activo' : '❌ Inactivo'}
                      </span>
                    </td>
                    <td>
                      {gymService.category ? (
                        <Link to={`/category/${gymService.category.id}`}>{gymService.category.categoryName}</Link>
                      ) : (
                        ''
                      )}
                    </td>

                    {/* Columna de acción para usuarios normales */}
                    {!isAdmin && (
                      <td>
                        {alreadyBought ? (
                          <span
                            style={{
                              background: '#dcfce7',
                              color: '#15803d',
                              padding: '6px 14px',
                              borderRadius: '999px',
                              fontSize: '13px',
                              fontWeight: 600,
                            }}
                          >
                            ✅ Adquirido
                          </span>
                        ) : (
                          <button
                            onClick={() => handleBuy(gymService)}
                            style={{
                              background: 'linear-gradient(135deg, #1a73e8, #0d47a1)',
                              color: '#fff',
                              border: 'none',
                              borderRadius: '10px',
                              padding: '7px 16px',
                              fontWeight: 700,
                              fontSize: '13px',
                              cursor: 'pointer',
                            }}
                          >
                            🛒 Comprar
                          </button>
                        )}
                      </td>
                    )}

                    {/* Acciones admin */}
                    {isAdmin && (
                      <td className="text-end">
                        <div className="btn-group flex-btn-group-container">
                          <Button tag={Link} to={`/gym-service/${gymService.id}`} color="info" size="sm">
                            <FontAwesomeIcon icon="eye" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.view" />
                            </span>
                          </Button>
                          <Button
                            tag={Link}
                            to={`/gym-service/${gymService.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                            color="primary"
                            size="sm"
                          >
                            <FontAwesomeIcon icon="pencil-alt" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.edit" />
                            </span>
                          </Button>
                          <Button
                            onClick={() =>
                              (window.location.href = `/gym-service/${gymService.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                            }
                            color="danger"
                            size="sm"
                          >
                            <FontAwesomeIcon icon="trash" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.delete" />
                            </span>
                          </Button>
                        </div>
                      </td>
                    )}
                  </tr>
                );
              })}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="gymtrackApp.gymService.home.notFound">No Gym Services found</Translate>
            </div>
          )
        )}
      </div>

      {totalItems ? (
        <div className={gymServiceList && gymServiceList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default GymService;
