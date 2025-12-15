import dayjs from 'dayjs';

export interface IIncident {
  id?: number;
  incidentType?: string;
  description?: string | null;
  reportedDate?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IIncident> = {};
