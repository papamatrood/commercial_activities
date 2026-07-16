import dayjs from 'dayjs/esm';

import { ICompany } from 'app/entities/company/company.model';
import { ISale } from 'app/entities/sale/sale.model';

export interface IDebt {
  id: number;
  totalAmount?: number | null;
  amountPaid?: number | null;
  remainingAmount?: number | null;
  date?: dayjs.Dayjs | null;
  company?: Pick<ICompany, 'id'> | null;
  sale?: Pick<ISale, 'id'> | null;
}

export type NewDebt = Omit<IDebt, 'id'> & { id: null };
