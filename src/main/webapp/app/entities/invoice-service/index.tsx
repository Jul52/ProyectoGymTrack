import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import InvoiceService from './invoice-service';
import InvoiceServiceDetail from './invoice-service-detail';
import InvoiceServiceUpdate from './invoice-service-update';
import InvoiceServiceDeleteDialog from './invoice-service-delete-dialog';

const InvoiceServiceRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<InvoiceService />} />
    <Route path="new" element={<InvoiceServiceUpdate />} />
    <Route path=":id">
      <Route index element={<InvoiceServiceDetail />} />
      <Route path="edit" element={<InvoiceServiceUpdate />} />
      <Route path="delete" element={<InvoiceServiceDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InvoiceServiceRoutes;
