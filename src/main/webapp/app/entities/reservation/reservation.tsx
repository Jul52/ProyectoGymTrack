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
import { AUTHORITIES } from 'app/config/constants';
import { getEntities } from './reservation.reducer';
import { IReservation } from 'app/shared/model/reservation.model';
import dayjs from 'dayjs';

export const Reservation = () => {
  const dispatch = useAppDispatch();
  const location = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(location, ITEMS_PER_PAGE, 'id'), location.search),
  );

  const reservationList = useAppSelector(state => state.reservation.entities);
  const totalItems = useAppSelector(state => state.reservation.totalItems);
  const loading = useAppSelector(state => state.reservation.loading);

  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const isAdmin = isAuthenticated && hasAnyAuthority(account.authorities, [AUTHORITIES.ADMIN]);

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
    if (location.search !== endURL) navigate(`${location.pathname}${endURL}`);
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
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
  }, [location.search]);

  const sort = (field: keyof IReservation) => () =>
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: field,
    });

  const handlePagination = (currentPage: number) => setPaginationState({ ...paginationState, activePage: currentPage });

  const getSortIconByFieldName = (fieldName: string) => {
    if (paginationState.sort !== fieldName) return faSort;
    return paginationState.order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2>
        <Translate contentKey="gymtrackApp.reservation.home.title">Reservations</Translate>
        {isAdmin && (
          <Link to="/reservation/new" className="btn btn-primary float-end">
            <FontAwesomeIcon icon="plus" />{' '}
            <Translate contentKey="gymtrackApp.reservation.home.createLabel">Create new Reservation</Translate>
          </Link>
        )}
      </h2>

      <div className="table-responsive">
        {reservationList && reservationList.length > 0 ? (
          <>
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="gymtrackApp.reservation.id">ID</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('status')}>
                    <Translate contentKey="gymtrackApp.reservation.status">Status</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                  </th>
                  <th>
                    <Translate contentKey="gymtrackApp.reservation.course">Course</Translate>
                  </th>
                  <th>
                    <Translate contentKey="gymtrackApp.reservation.gymService">Gym Service</Translate>
                  </th>
                  <th>
                    <Translate contentKey="gymtrackApp.reservation.registeredBy">User</Translate>
                  </th>
                  <th>
                    <Translate contentKey="gymtrackApp.reservation.schedule">Schedule</Translate>
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {reservationList.map((reservation: IReservation, i: number) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`/reservation/${reservation.id}`} color="link" size="sm">
                        {reservation.id}
                      </Button>
                    </td>
                    <td>{reservation.status ? 'true' : 'false'}</td>
                    <td>{reservation.course?.courseName || '-'}</td>
                    <td>{reservation.gymService?.serviceName || '-'}</td>
                    <td>
                      {reservation.registeredBy ? `${reservation.registeredBy.firstName} ${reservation.registeredBy.firstLastName}` : '-'}
                    </td>
                    <td>
                      {reservation.schedule?.startTime
                        ? `${reservation.schedule.dayOfWeek ?? '-'} - ${dayjs(reservation.schedule.startTime).format('HH:mm')}`
                        : '-'}
                    </td>
                    <td className="text-end">
                      <div className="btn-group">
                        <Button tag={Link} to={`/reservation/${reservation.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />
                        </Button>
                        {isAdmin && (
                          <>
                            <Button tag={Link} to={`/reservation/${reservation.id}/edit`} color="primary" size="sm">
                              <FontAwesomeIcon icon="pencil-alt" />
                            </Button>
                            <Button tag={Link} to={`/reservation/${reservation.id}/delete`} color="danger" size="sm">
                              <FontAwesomeIcon icon="trash" />
                            </Button>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>

            <div className="d-flex justify-content-center">
              <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
            </div>

            <div className="d-flex justify-content-center">
              <JhiPagination
                activePage={paginationState.activePage}
                onSelect={handlePagination}
                maxButtons={5}
                itemsPerPage={paginationState.itemsPerPage}
                totalItems={totalItems}
              />
            </div>
          </>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="gymtrackApp.reservation.home.notFound">No Reservations found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Reservation;
