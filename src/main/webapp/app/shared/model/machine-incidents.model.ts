import { IIncident } from 'app/shared/model/incident.model';
import { IMachine } from 'app/shared/model/machine.model';

export interface IMachineIncidents {
  id?: number;
  description?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  video?: string | null;
  incident?: IIncident;
  machine?: IMachine;
}

export const defaultValue: Readonly<IMachineIncidents> = {};
