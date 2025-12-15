import { ICourse } from 'app/shared/model/course.model';

export interface IZone {
  id?: number;
  name?: string;
  status?: boolean;
  courses?: ICourse[];
}

export const defaultValue: Readonly<IZone> = {
  status: false,
};
