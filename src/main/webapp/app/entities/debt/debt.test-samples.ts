import dayjs from 'dayjs/esm';

import { IDebt, NewDebt } from './debt.model';

export const sampleWithRequiredData: IDebt = {
  id: 15474,
  totalAmount: 4773.19,
  date: dayjs('2026-07-16T16:52'),
};

export const sampleWithPartialData: IDebt = {
  id: 21414,
  totalAmount: 23398.62,
  date: dayjs('2026-07-16T03:50'),
};

export const sampleWithFullData: IDebt = {
  id: 12632,
  totalAmount: 6536.32,
  amountPaid: 3633.92,
  remainingAmount: 23730.71,
  date: dayjs('2026-07-16T21:10'),
};

export const sampleWithNewData: NewDebt = {
  totalAmount: 16982.1,
  date: dayjs('2026-07-16T10:57'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
