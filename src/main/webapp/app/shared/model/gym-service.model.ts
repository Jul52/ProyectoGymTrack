import { ICategory } from 'app/shared/model/category.model';

export interface IGymService {
  id?: number;
  serviceName?: string;
  serviceDescription?: string | null;
  price?: number;
  status?: boolean;
  category?: ICategory;
}

export const defaultValue: Readonly<IGymService> = {
  status: false,
};
