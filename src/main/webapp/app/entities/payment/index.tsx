import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Payment from './payment';
import PaymentDetail from './payment-detail';
import PaymentUpdate from './payment-update';
import PrivateRoute from 'app/shared/auth/private-route';
import PaymentDeleteDialog from 'app/entities/payment/payment-delete-dialog';

const PaymentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Payment />} />
    <Route
      path="new"
      element={
        <PrivateRoute hasAnyAuthorities={['ROLE_ADMIN', 'ROLE_USER', 'ROLE_TRAINER']}>
          <PaymentUpdate />
        </PrivateRoute>
      }
    />
    <Route path=":id">
      <Route index element={<PaymentDetail />} />
      <Route
        path="edit"
        element={
          <PrivateRoute hasAnyAuthorities={['ROLE_ADMIN']}>
            <PaymentUpdate />
          </PrivateRoute>
        }
      />
      <Route
        path="delete"
        element={
          <PrivateRoute hasAnyAuthorities={['ROLE_ADMIN']}>
            <PaymentDeleteDialog />
          </PrivateRoute>
        }
      />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PaymentRoutes;
