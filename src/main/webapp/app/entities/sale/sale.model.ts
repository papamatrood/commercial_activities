import dayjs from 'dayjs/esm';

import { IAppUser } from 'app/entities/app-user/app-user.model';
import { ICompany } from 'app/entities/company/company.model';

export interface ISale {
  id: number;
  date?: dayjs.Dayjs | null;
  amountToPay?: number | null;
  amountPaid?: number | null;
  remainingAmount?: number | null;
  customerName?: string | null;
  customerCompany?: string | null;
  customerContact?: string | null;
  company?: Pick<ICompany, 'id'> | null;
  seller?: Pick<IAppUser, 'id'> | null;
}

export type NewSale = Omit<ISale, 'id'> & { id: null };
