import React from 'react';
import { Route } from 'react-router';
import { useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Invoice from './invoice';
import InvoiceDetail from './invoice-detail';
import InvoiceUpdate from './invoice-update';
import InvoiceDeleteDialog from './invoice-delete-dialog';

const InvoiceRoutes = () => {
  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = account.authorities?.includes(AUTHORITIES.ADMIN);

  return (
    <ErrorBoundaryRoutes>
      <Route index element={<Invoice />} />
      {isAdmin && <Route path="new" element={<InvoiceUpdate />} />}
      <Route path=":id">
        <Route index element={<InvoiceDetail />} />
        {isAdmin && <Route path="edit" element={<InvoiceUpdate />} />}
        {isAdmin && <Route path="delete" element={<InvoiceDeleteDialog />} />}
      </Route>
    </ErrorBoundaryRoutes>
  );
};

export default InvoiceRoutes;
