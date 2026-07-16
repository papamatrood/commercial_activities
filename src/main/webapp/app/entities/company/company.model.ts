import dayjs from 'dayjs/esm';

import { CompanyStatusEnum } from 'app/entities/enumerations/company-status-enum.model';

export interface ICompany {
  id: number;
  name?: string | null;
  location?: string | null;
  creationDate?: dayjs.Dayjs | null;
  status?: keyof typeof CompanyStatusEnum | null;
}

export type NewCompany = Omit<ICompany, 'id'> & { id: null };
