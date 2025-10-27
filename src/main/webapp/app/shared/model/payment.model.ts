import dayjs from 'dayjs';
import { IPaymentMethod } from 'app/shared/model/payment-method.model';
import { IUserData } from 'app/shared/model/user-data.model';

export interface IPayment {
  id?: number;
  amountPaid?: number;
  paymentDate?: dayjs.Dayjs;
  transactionId?: string | null;
  status?: string;
  paymentMethod?: IPaymentMethod;
  registeredBy?: IUserData;
}

export const defaultValue: Readonly<IPayment> = {};
