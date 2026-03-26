import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import axios from 'axios';

import { getEntities } from './invoice.reducer';

export const Invoice = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [myInvoices, setMyInvoices] = useState([]);

  const invoiceList = useAppSelector(state => state.invoice.entities);
  const loading = useAppSelector(state => state.invoice.loading);
  const totalItems = useAppSelector(state => state.invoice.totalItems);
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

  const loadMyInvoices = async () => {
    try {
      const res = await axios.get('/api/invoices/my', {
        params: {
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        },
      });
      setMyInvoices(res.data);
    } catch (e) {
      console.error('Error cargando facturas', e);
    }
  };

  const sortEntities = () => {
    if (isAdmin) {
      getAllEntities();
      const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
      if (pageLocation.search !== endURL) navigate(`${pageLocation.pathname}${endURL}`);
    } else {
      loadMyInvoices();
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort, isAdmin]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({ ...paginationState, activePage: +page, sort: sortSplit[0], order: sortSplit[1] });
    }
  }, [pageLocation.search]);

  const sort = p => () => setPaginationState({ ...paginationState, order: paginationState.order === ASC ? DESC : ASC, sort: p });
  const handlePagination = currentPage => setPaginationState({ ...paginationState, activePage: currentPage });
  const handleSyncList = () => sortEntities();
  const getSortIconByFieldName = (fieldName: string) => {
    if (paginationState.sort !== fieldName) return faSort;
    return paginationState.order === ASC ? faSortUp : faSortDown;
  };

  const displayList = isAdmin ? invoiceList : myInvoices;

  return (
    <div>
      <h2 id="invoice-heading" data-cy="InvoiceHeading">
        <Translate contentKey="gymtrackApp.invoice.home.title">Invoices</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gymtrackApp.invoice.home.refreshListLabel">Refresh List</Translate>
          </Button>
          {isAdmin && (
            <Link to="/invoice/new" className="btn btn-primary jh-create-entity" data-cy="entityCreateButton">
              <FontAwesomeIcon icon="plus" />
              &nbsp;
              <Translate contentKey="gymtrackApp.invoice.home.createLabel">Create new Invoice</Translate>
            </Link>
          )}
        </div>
      </h2>
      <div className="table-responsive">
        {displayList && displayList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="gymtrackApp.invoice.id" /> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('total')}>
                  <Translate contentKey="gymtrackApp.invoice.total" /> <FontAwesomeIcon icon={getSortIconByFieldName('total')} />
                </th>
                <th className="hand" onClick={sort('createdDate')}>
                  <Translate contentKey="gymtrackApp.invoice.createdDate" />{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdDate')} />
                </th>
                <th>
                  <Translate contentKey="gymtrackApp.invoice.paymentMethod" /> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="gymtrackApp.invoice.service" /> <FontAwesomeIcon icon="sort" />
                </th>
                {isAdmin && (
                  <th>
                    <Translate contentKey="gymtrackApp.invoice.userData" /> <FontAwesomeIcon icon="sort" />
                  </th>
                )}
                <th />
              </tr>
            </thead>
            <tbody>
              {displayList.map((invoice, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/invoice/${invoice.id}`} color="link" size="sm">
                      {invoice.id}
                    </Button>
                  </td>
                  <td>{invoice.total}</td>
                  <td>{invoice.createdDate ? <TextFormat type="date" value={invoice.createdDate} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    {invoice.paymentMethod ? (
                      <Link to={`/payment-method/${invoice.paymentMethod.id}`}>{invoice.paymentMethod.methodName}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{invoice.service ? <Link to={`/gym-service/${invoice.service.id}`}>{invoice.service.serviceName}</Link> : ''}</td>
                  {isAdmin && (
                    <td>{invoice.userData ? <Link to={`/user-data/${invoice.userData.id}`}>{invoice.userData.document}</Link> : ''}</td>
                  )}
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/invoice/${invoice.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view" />
                        </span>
                      </Button>
                      {isAdmin && (
                        <>
                          <Button
                            tag={Link}
                            to={`/invoice/${invoice.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                              (window.location.href = `/invoice/${invoice.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                            }
                            color="danger"
                            size="sm"
                          >
                            <FontAwesomeIcon icon="trash" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.delete" />
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
              <Translate contentKey="gymtrackApp.invoice.home.notFound">No Invoices found</Translate>
            </div>
          )
        )}
      </div>
      {isAdmin && totalItems ? (
        <div className={displayList.length > 0 ? '' : 'd-none'}>
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

export default Invoice;
