import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MachineIncidents from './machine-incidents';
import MachineIncidentsDetail from './machine-incidents-detail';
import MachineIncidentsUpdate from './machine-incidents-update';
import MachineIncidentsDeleteDialog from './machine-incidents-delete-dialog';

const MachineIncidentsRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MachineIncidents />} />
    <Route path="new" element={<MachineIncidentsUpdate />} />
    <Route path=":id">
      <Route index element={<MachineIncidentsDetail />} />
      <Route path="edit" element={<MachineIncidentsUpdate />} />
      <Route path="delete" element={<MachineIncidentsDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MachineIncidentsRoutes;
