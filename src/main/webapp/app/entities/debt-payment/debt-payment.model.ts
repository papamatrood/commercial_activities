import dayjs from 'dayjs/esm';

import { IDebt } from 'app/entities/debt/debt.model';

export interface IDebtPayment {
  id: number;
  amountPaid?: number | null;
  remainingAmount?: number | null;
  date?: dayjs.Dayjs | null;
  debt?: Pick<IDebt, 'id'> | null;
}

export type NewDebtPayment = Omit<IDebtPayment, 'id'> & { id: null };
