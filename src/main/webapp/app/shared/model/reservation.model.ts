import dayjs from 'dayjs';
import { ICourse } from 'app/shared/model/course.model';
import { IGymService } from 'app/shared/model/gym-service.model';
import { IUserData } from 'app/shared/model/user-data.model';

export interface IReservation {
  id?: number;
  status?: boolean;
  description?: string | null;
  reservationDate?: dayjs.Dayjs;
  course?: ICourse;
  gymService?: IGymService;
  userData?: IUserData;
}

export const defaultValue: Readonly<IReservation> = {
  status: false,
};
