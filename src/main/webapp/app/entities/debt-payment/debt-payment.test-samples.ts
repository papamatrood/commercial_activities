import dayjs from 'dayjs/esm';

import { IDebtPayment, NewDebtPayment } from './debt-payment.model';

export const sampleWithRequiredData: IDebtPayment = {
  id: 13641,
  amountPaid: 20748.36,
  date: dayjs('2026-07-16T12:02'),
};

export const sampleWithPartialData: IDebtPayment = {
  id: 12515,
  amountPaid: 19735.09,
  remainingAmount: 18072.31,
  date: dayjs('2026-07-16T02:01'),
};

export const sampleWithFullData: IDebtPayment = {
  id: 2144,
  amountPaid: 32745.96,
  remainingAmount: 24128.47,
  date: dayjs('2026-07-16T05:36'),
};

export const sampleWithNewData: NewDebtPayment = {
  amountPaid: 22313.55,
  date: dayjs('2026-07-16T12:01'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
