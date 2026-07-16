import dayjs from 'dayjs/esm';

import { ICashCollection, NewCashCollection } from './cash-collection.model';

export const sampleWithRequiredData: ICashCollection = {
  id: 32376,
  date: dayjs('2026-07-16T17:35'),
  amount: 6797.55,
};

export const sampleWithPartialData: ICashCollection = {
  id: 7082,
  date: dayjs('2026-07-16T09:38'),
  amount: 7850.85,
};

export const sampleWithFullData: ICashCollection = {
  id: 18127,
  date: dayjs('2026-07-16T09:07'),
  amount: 8890.41,
};

export const sampleWithNewData: NewCashCollection = {
  date: dayjs('2026-07-16T12:35'),
  amount: 8024.27,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
