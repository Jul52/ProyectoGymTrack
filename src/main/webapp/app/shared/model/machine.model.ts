import { IUserData } from 'app/shared/model/user-data.model';

export interface IMachine {
  id?: number;
  serial?: string;
  description?: string;
  status?: boolean;
  admin?: IUserData;
}

export const defaultValue: Readonly<IMachine> = {
  status: false,
};
