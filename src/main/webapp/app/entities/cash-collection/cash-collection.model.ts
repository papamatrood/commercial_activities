import dayjs from 'dayjs/esm';

import { IAppUser } from 'app/entities/app-user/app-user.model';
import { ICompany } from 'app/entities/company/company.model';

export interface ICashCollection {
  id: number;
  date?: dayjs.Dayjs | null;
  amount?: number | null;
  company?: Pick<ICompany, 'id'> | null;
  user?: Pick<IAppUser, 'id'> | null;
}

export type NewCashCollection = Omit<ICashCollection, 'id'> & { id: null };
