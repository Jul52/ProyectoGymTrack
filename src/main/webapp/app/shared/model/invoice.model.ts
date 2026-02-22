import dayjs from 'dayjs';
import { IPayment } from 'app/shared/model/payment.model';
import { IPaymentMethod } from 'app/shared/model/payment-method.model';
import { IUserData } from 'app/shared/model/user-data.model';
import { IGymService } from 'app/shared/model/gym-service.model';

export interface IInvoice {
  id?: number;
  total?: number;
  createdDate?: dayjs.Dayjs | null;
  payment?: IPayment | null;
  paymentMethod?: IPaymentMethod;
  userData?: IUserData;
  service?: IGymService | null;
}

export const defaultValue: Readonly<IInvoice> = {};
