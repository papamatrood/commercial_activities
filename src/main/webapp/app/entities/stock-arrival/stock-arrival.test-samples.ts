import dayjs from 'dayjs/esm';

import { IStockArrival, NewStockArrival } from './stock-arrival.model';

export const sampleWithRequiredData: IStockArrival = {
  id: 15695,
  quantity: 10537,
  date: dayjs('2026-07-16T13:11'),
};

export const sampleWithPartialData: IStockArrival = {
  id: 6655,
  quantity: 6094,
  date: dayjs('2026-07-15T22:23'),
};

export const sampleWithFullData: IStockArrival = {
  id: 32375,
  barcode: 'commissionnaire affable soutenir',
  quantity: 26145,
  amount: 30090.65,
  date: dayjs('2026-07-16T18:13'),
};

export const sampleWithNewData: NewStockArrival = {
  quantity: 11315,
  date: dayjs('2026-07-16T11:07'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
