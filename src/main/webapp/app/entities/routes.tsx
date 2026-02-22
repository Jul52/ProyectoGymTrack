import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Payment from './payment';
import UserData from './user-data';
import DocumentType from './document-type';
import Reservation from './reservation';
import Machine from './machine';
import Incident from './incident';
import MachineIncidents from './machine-incidents';
import Category from './category';
import PaymentMethod from './payment-method';
import GymService from './gym-service';
import Invoice from './invoice';
import InvoiceService from './invoice-service';
import Course from './course';
import Schedule from './schedule';
import Zone from './zone';
import PaymentRoutes from './payment';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="payment/*" element={<Payment />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
        <Route path="payment/*" element={<PaymentRoutes />} />
        <Route path="user-data/*" element={<UserData />} />
        <Route path="document-type/*" element={<DocumentType />} />
        <Route path="reservation/*" element={<Reservation />} />
        <Route path="machine/*" element={<Machine />} />
        <Route path="incident/*" element={<Incident />} />
        <Route path="machine-incidents/*" element={<MachineIncidents />} />
        <Route path="category/*" element={<Category />} />
        <Route path="payment-method/*" element={<PaymentMethod />} />
        <Route path="gym-service/*" element={<GymService />} />
        <Route path="invoice/*" element={<Invoice />} />
        <Route path="invoice-service/*" element={<InvoiceService />} />
        <Route path="course/*" element={<Course />} />
        <Route path="schedule/*" element={<Schedule />} />
        <Route path="zone/*" element={<Zone />} />
      </ErrorBoundaryRoutes>
    </div>
  );
};
