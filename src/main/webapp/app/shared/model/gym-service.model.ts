import { ICategory } from 'app/shared/model/category.model';

export type CourseAccessType = 'NONE' | 'LIMITED' | 'UNLIMITED';

export interface IGymService {
  id?: number;
  serviceName?: string;
  serviceDescription?: string | null;
  price?: number;
  status?: boolean;
  courseAccessType?: CourseAccessType;
  maxClassesPerMonth?: number | null;
  category?: ICategory;
}

export const defaultValue: Readonly<IGymService> = {
  status: false,
  courseAccessType: 'NONE',
};
