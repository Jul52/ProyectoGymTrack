import payment from 'app/entities/payment/payment.reducer';
import userData from 'app/entities/user-data/user-data.reducer';
import documentType from 'app/entities/document-type/document-type.reducer';
import reservation from 'app/entities/reservation/reservation.reducer';
import machine from 'app/entities/machine/machine.reducer';
import incident from 'app/entities/incident/incident.reducer';
import machineIncidents from 'app/entities/machine-incidents/machine-incidents.reducer';
import category from 'app/entities/category/category.reducer';
import paymentMethod from 'app/entities/payment-method/payment-method.reducer';
import gymService from 'app/entities/gym-service/gym-service.reducer';
import invoice from 'app/entities/invoice/invoice.reducer';
import invoiceService from 'app/entities/invoice-service/invoice-service.reducer';
import course from 'app/entities/course/course.reducer';
import schedule from 'app/entities/schedule/schedule.reducer';
import zone from 'app/entities/zone/zone.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  payment,
  userData,
  documentType,
  reservation,
  machine,
  incident,
  machineIncidents,
  category,
  paymentMethod,
  gymService,
  invoice,
  invoiceService,
  course,
  schedule,
  zone,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
