import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IDocumentType } from 'app/shared/model/document-type.model';

export interface IUserData {
  id?: number;
  firstName?: string;
  secondName?: string | null;
  firstLastName?: string;
  secondLastName?: string | null;
  documentNumber?: string;
  phone?: string;
  birthDate?: dayjs.Dayjs | null;
  user?: IUser;
  documentType?: IDocumentType;
}

export const defaultValue: Readonly<IUserData> = {};
