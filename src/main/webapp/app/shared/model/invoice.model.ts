import dayjs from 'dayjs';
import { IPayment } from 'app/shared/model/payment.model';
import { IPaymentMethod } from 'app/shared/model/payment-method.model';
import { IUserData } from 'app/shared/model/user-data.model';

export interface IInvoice {
  id?: number;
  total?: number;
  createdDate?: dayjs.Dayjs | null;
  payment?: IPayment | null;
  paymentMethod?: IPaymentMethod;
  userData?: IUserData;
}

export const defaultValue: Readonly<IInvoice> = {};
