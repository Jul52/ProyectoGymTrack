import dayjs from 'dayjs';
import { IZone } from 'app/shared/model/zone.model';
import { IUserData } from 'app/shared/model/user-data.model';

export interface ICourse {
  id?: number;
  courseName?: string;
  status?: boolean;
  startDate?: dayjs.Dayjs;
  endDate?: dayjs.Dayjs;
  capacity?: number | null;
  zones?: IZone[];
  trainer?: IUserData;
}

export const defaultValue: Readonly<ICourse> = {
  status: false,
};
