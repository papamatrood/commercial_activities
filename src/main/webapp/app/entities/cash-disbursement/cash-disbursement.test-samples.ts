import dayjs from 'dayjs/esm';

import { ICashDisbursement, NewCashDisbursement } from './cash-disbursement.model';

export const sampleWithRequiredData: ICashDisbursement = {
  id: 6006,
  reason: 'justifier absolument',
  amount: 16940.17,
  date: dayjs('2026-07-16T21:09'),
};

export const sampleWithPartialData: ICashDisbursement = {
  id: 9660,
  reason: 'nonobstant insister',
  amount: 25820.68,
  date: dayjs('2026-07-16T14:48'),
};

export const sampleWithFullData: ICashDisbursement = {
  id: 25254,
  reason: 'toc',
  amount: 22867.09,
  date: dayjs('2026-07-16T05:23'),
};

export const sampleWithNewData: NewCashDisbursement = {
  reason: 'sitôt que dynamique sale',
  amount: 19156.49,
  date: dayjs('2026-07-16T01:34'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
