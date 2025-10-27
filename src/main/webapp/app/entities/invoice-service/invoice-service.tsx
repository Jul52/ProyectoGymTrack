import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './invoice-service.reducer';

export const InvoiceService = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const invoiceServiceList = useAppSelector(state => state.invoiceService.entities);
  const loading = useAppSelector(state => state.invoiceService.loading);
  const totalItems = useAppSelector(state => state.invoiceService.totalItems);

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
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="invoice-service-heading" data-cy="InvoiceServiceHeading">
        <Translate contentKey="gymtrackApp.invoiceService.home.title">Invoice Services</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gymtrackApp.invoiceService.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/invoice-service/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="gymtrackApp.invoiceService.home.createLabel">Create new Invoice Service</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {invoiceServiceList && invoiceServiceList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="gymtrackApp.invoiceService.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('quantity')}>
                  <Translate contentKey="gymtrackApp.invoiceService.quantity">Quantity</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('quantity')} />
                </th>
                <th className="hand" onClick={sort('subtotal')}>
                  <Translate contentKey="gymtrackApp.invoiceService.subtotal">Subtotal</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('subtotal')} />
                </th>
                <th className="hand" onClick={sort('salePrice')}>
                  <Translate contentKey="gymtrackApp.invoiceService.salePrice">Sale Price</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('salePrice')} />
                </th>
                <th>
                  <Translate contentKey="gymtrackApp.invoiceService.invoice">Invoice</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="gymtrackApp.invoiceService.service">Service</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {invoiceServiceList.map((invoiceService, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/invoice-service/${invoiceService.id}`} color="link" size="sm">
                      {invoiceService.id}
                    </Button>
                  </td>
                  <td>{invoiceService.quantity}</td>
                  <td>{invoiceService.subtotal}</td>
                  <td>{invoiceService.salePrice}</td>
                  <td>
                    {invoiceService.invoice ? <Link to={`/invoice/${invoiceService.invoice.id}`}>{invoiceService.invoice.id}</Link> : ''}
                  </td>
                  <td>
                    {invoiceService.service ? (
                      <Link to={`/gym-service/${invoiceService.service.id}`}>{invoiceService.service.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/invoice-service/${invoiceService.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/invoice-service/${invoiceService.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/invoice-service/${invoiceService.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="gymtrackApp.invoiceService.home.notFound">No Invoice Services found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={invoiceServiceList && invoiceServiceList.length > 0 ? '' : 'd-none'}>
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

export default InvoiceService;
