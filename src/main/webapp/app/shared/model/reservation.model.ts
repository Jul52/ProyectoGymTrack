import { ICourse } from 'app/shared/model/course.model';
import { IGymService } from 'app/shared/model/gym-service.model';
import { IUserData } from 'app/shared/model/user-data.model';
import { ISchedule } from 'app/shared/model/schedule.model';

export interface IReservation {
  id?: number;
  status?: boolean;
  course?: ICourse;
  gymService?: IGymService;
  registeredBy?: IUserData;
  schedule?: ISchedule;
}

export const defaultValue: Readonly<IReservation> = {
  status: false,
};
