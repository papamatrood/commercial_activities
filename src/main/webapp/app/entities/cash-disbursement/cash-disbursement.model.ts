import dayjs from 'dayjs/esm';

import { IAppUser } from 'app/entities/app-user/app-user.model';
import { ICompany } from 'app/entities/company/company.model';

export interface ICashDisbursement {
  id: number;
  reason?: string | null;
  amount?: number | null;
  date?: dayjs.Dayjs | null;
  company?: Pick<ICompany, 'id'> | null;
  user?: Pick<IAppUser, 'id'> | null;
}

export type NewCashDisbursement = Omit<ICashDisbursement, 'id'> & { id: null };
