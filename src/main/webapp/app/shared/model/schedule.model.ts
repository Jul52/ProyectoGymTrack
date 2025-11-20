import dayjs from 'dayjs';
import { ICourse } from 'app/shared/model/course.model';

export interface ISchedule {
  id?: number;
  dayOfWeek?: string;
  startTime?: dayjs.Dayjs;
  endTime?: dayjs.Dayjs;
  course?: ICourse;
}

export const defaultValue: Readonly<ISchedule> = {};
