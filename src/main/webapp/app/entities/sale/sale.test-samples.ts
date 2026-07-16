import dayjs from 'dayjs/esm';

import { ISale, NewSale } from './sale.model';

export const sampleWithRequiredData: ISale = {
  id: 2433,
  date: dayjs('2026-07-16T07:38'),
  amountToPay: 3265.17,
  amountPaid: 12029.72,
};

export const sampleWithPartialData: ISale = {
  id: 22273,
  date: dayjs('2026-07-16T16:39'),
  amountToPay: 1623.51,
  amountPaid: 13932.94,
  customerCompany: 'équipe gratis clac',
  customerContact: 'boum triste',
};

export const sampleWithFullData: ISale = {
  id: 29002,
  date: dayjs('2026-07-15T22:25'),
  amountToPay: 21063.11,
  amountPaid: 3009.53,
  remainingAmount: 20194.22,
  customerName: 'alors que joliment passablement',
  customerCompany: 'devant',
  customerContact: 'ah membre de l’équipe',
};

export const sampleWithNewData: NewSale = {
  date: dayjs('2026-07-16T04:56'),
  amountToPay: 11647.53,
  amountPaid: 30641.01,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
