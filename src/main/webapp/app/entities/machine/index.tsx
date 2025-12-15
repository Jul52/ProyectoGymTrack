import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Machine from './machine';
import MachineDetail from './machine-detail';
import MachineUpdate from './machine-update';
import MachineDeleteDialog from './machine-delete-dialog';

const MachineRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Machine />} />
    <Route path="new" element={<MachineUpdate />} />
    <Route path=":id">
      <Route index element={<MachineDetail />} />
      <Route path="edit" element={<MachineUpdate />} />
      <Route path="delete" element={<MachineDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MachineRoutes;
