import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserData from './user-data';
import UserDataDetail from './user-data-detail';
import UserDataUpdate from './user-data-update';
import UserDataDeleteDialog from './user-data-delete-dialog';

const UserDataRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserData />} />
    <Route path="new" element={<UserDataUpdate />} />
    <Route path=":id">
      <Route index element={<UserDataDetail />} />
      <Route path="edit" element={<UserDataUpdate />} />
      <Route path="delete" element={<UserDataDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserDataRoutes;
