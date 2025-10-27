import { IInvoice } from 'app/shared/model/invoice.model';
import { IGymService } from 'app/shared/model/gym-service.model';

export interface IInvoiceService {
  id?: number;
  quantity?: number;
  subtotal?: number;
  salePrice?: number;
  invoice?: IInvoice;
  service?: IGymService;
}

export const defaultValue: Readonly<IInvoiceService> = {};
