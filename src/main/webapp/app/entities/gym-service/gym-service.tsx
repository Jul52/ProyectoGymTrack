import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

import { getEntities } from './gym-service.reducer';

export const GymService = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const account = useAppSelector(state => state.authentication.account);
  const isUser = hasAnyAuthority(account.authorities, ['ROLE_USER']);
  const isAdmin = hasAnyAuthority(account.authorities, ['ROLE_ADMIN']);

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const gymServiceList = useAppSelector(state => state.gymService.entities);
  const loading = useAppSelector(state => state.gymService.loading);
  const totalItems = useAppSelector(state => state.gymService.totalItems);

  const isUserOrAdmin = account?.authorities?.includes('ROLE_USER') || account?.authorities?.includes('ROLE_ADMIN');
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
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
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
  };

  const getSortIconByFieldName = (fieldName: string) => {
    if (paginationState.sort !== fieldName) return faSort;
    return paginationState.order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="gym-service-heading">
        <Translate contentKey="gymtrackApp.gymService.home.title">Gym Services</Translate>

        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gymtrackApp.gymService.home.refreshListLabel">Refresh List</Translate>
          </Button>

          {isAdmin && (
            <Link to="/gym-service/new" className="btn btn-primary">
              <FontAwesomeIcon icon="plus" />{' '}
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
                <th onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th onClick={sort('serviceName')}>
                  <Translate contentKey="gymtrackApp.gymService.serviceName" />
                </th>
                <th onClick={sort('serviceDescription')}>
                  <Translate contentKey="gymtrackApp.gymService.serviceDescription" />
                </th>
                <th onClick={sort('price')}>
                  <Translate contentKey="gymtrackApp.gymService.price" />
                </th>
                <th onClick={sort('status')}>
                  <Translate contentKey="gymtrackApp.gymService.status" />
                </th>
                <th>
                  <Translate contentKey="gymtrackApp.gymService.category" />
                </th>
                <th />
              </tr>
            </thead>

            <tbody>
              {gymServiceList.map((gymService, i) => (
                <tr key={`entity-${i}`}>
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

                  <td className="text-end">
                    <div className="btn-group">
                      {/* Ver - todos */}
                      <Button tag={Link} to={`/gym-service/${gymService.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" /> Ver
                      </Button>

                      {isAdmin && (
                        <>
                          <Button tag={Link} to={`/gym-service/${gymService.id}/edit`} color="primary" size="sm">
                            <FontAwesomeIcon icon="pencil-alt" /> Editar
                          </Button>

                          <Button onClick={() => (window.location.href = `/gym-service/${gymService.id}/delete`)} color="danger" size="sm">
                            <FontAwesomeIcon icon="trash" /> Eliminar
                          </Button>
                        </>
                      )}

                      {isUser && gymService.status && (
                        <Button tag={Link} to={`/payment/new?serviceId=${gymService.id}`} color="success" size="sm">
                          <FontAwesomeIcon icon="credit-card" /> Pagar
                        </Button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Gym Services found</div>
        )}
      </div>

      {totalItems ? (
        <div>
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
