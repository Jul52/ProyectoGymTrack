import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Badge, Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import axios from 'axios';

import { getEntities } from './gym-service.reducer';

interface ServiceStatus {
  serviceId: number;
  serviceName: string;
  status: 'ACTIVE' | 'EXPIRED' | 'NOT_PURCHASED';
  purchaseDate?: string;
  expirationDate?: string;
}

export const GymService = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [serviceStatuses, setServiceStatuses] = useState<Record<number, ServiceStatus>>({});

  const gymServiceList = useAppSelector(state => state.gymService.entities);
  const loading = useAppSelector(state => state.gymService.loading);
  const totalItems = useAppSelector(state => state.gymService.totalItems);
  const isAdmin = useAppSelector(
    state => state.authentication.account.authorities && state.authentication.account.authorities.includes(AUTHORITIES.ADMIN),
  );

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const loadServiceStatuses = async () => {
    try {
      const response = await axios.get<ServiceStatus[]>('/api/gym-services/my-status');
      const statusMap: Record<number, ServiceStatus> = {};
      response.data.forEach(s => {
        statusMap[s.serviceId] = s;
      });
      setServiceStatuses(statusMap);
    } catch (e) {
      console.error('Error cargando estados de servicios', e);
    }
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
    if (!isAdmin) {
      loadServiceStatuses();
    }
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
    if (!isAdmin) loadServiceStatuses();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) return faSort;
    return order === ASC ? faSortUp : faSortDown;
  };

  const getStatusBadge = (serviceId: number) => {
    const s = serviceStatuses[serviceId];
    if (!s) return <Badge color="secondary">Sin contratar</Badge>;
    const colorMap = { ACTIVE: 'success', EXPIRED: 'danger', NOT_PURCHASED: 'secondary' };
    const labelMap = { ACTIVE: 'Activo', EXPIRED: 'Vencido', NOT_PURCHASED: 'Sin contratar' };
    return <Badge color={colorMap[s.status]}>{labelMap[s.status]}</Badge>;
  };

  // ← CAMBIO: ahora recibe serviceName y redirige a /payment/new
  const getActionButton = (serviceId: number, servicePrice: number, serviceName: string) => {
    const s = serviceStatuses[serviceId];
    const status = s?.status ?? 'NOT_PURCHASED';
    if (status === 'ACTIVE') return null;
    const label = status === 'EXPIRED' ? 'Renovar' : 'Contratar';
    const color = status === 'EXPIRED' ? 'warning' : 'success';
    return (
      <Button
        tag={Link}
        to={`/payment/new?serviceId=${serviceId}&price=${servicePrice}&serviceName=${encodeURIComponent(serviceName)}`}
        color={color}
        size="sm"
      >
        <FontAwesomeIcon icon="shopping-cart" /> {label}
      </Button>
    );
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
            <Link to="/gym-service/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
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
                  <Translate contentKey="gymtrackApp.gymService.serviceDescription">Service Description</Translate>{' '}
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
                  <Translate contentKey="gymtrackApp.gymService.category">Category</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                {!isAdmin && <th>Mi Estado</th>}
                <th />
              </tr>
            </thead>
            <tbody>
              {gymServiceList.map((gymService, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/gym-service/${gymService.id}`} color="link" size="sm">
                      {gymService.id}
                    </Button>
                  </td>
                  <td>{gymService.serviceName}</td>
                  <td>{gymService.serviceDescription}</td>
                  <td>{gymService.price}</td>
                  <td>{gymService.status ? 'Activo' : 'Inactivo'}</td>
                  <td>
                    {gymService.category ? <Link to={`/category/${gymService.category.id}`}>{gymService.category.categoryName}</Link> : ''}
                  </td>
                  {!isAdmin && <td>{getStatusBadge(gymService.id)}</td>}
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/gym-service/${gymService.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>

                      {/* ← CAMBIO: se pasa gymService.serviceName como tercer argumento */}
                      {!isAdmin && getActionButton(gymService.id, gymService.price, gymService.serviceName)}

                      {isAdmin && (
                        <>
                          <Button
                            tag={Link}
                            to={`/gym-service/${gymService.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                            color="primary"
                            size="sm"
                            data-cy="entityEditButton"
                          >
                            <FontAwesomeIcon icon="pencil-alt" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.edit">Edit</Translate>
                            </span>
                          </Button>
                          <Button
                            onClick={() =>
                              (window.location.href = `/gym-service/${gymService.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                            }
                            color="danger"
                            size="sm"
                            data-cy="entityDeleteButton"
                          >
                            <FontAwesomeIcon icon="trash" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.delete">Delete</Translate>
                            </span>
                          </Button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
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
