import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import GymService from './gym-service';
import GymServiceDetail from './gym-service-detail';
import GymServiceUpdate from './gym-service-update';
import GymServiceDeleteDialog from './gym-service-delete-dialog';

const GymServiceRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<GymService />} />
    <Route path="new" element={<GymServiceUpdate />} />
    <Route path=":id">
      <Route index element={<GymServiceDetail />} />
      <Route path="edit" element={<GymServiceUpdate />} />
      <Route path="delete" element={<GymServiceDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default GymServiceRoutes;
