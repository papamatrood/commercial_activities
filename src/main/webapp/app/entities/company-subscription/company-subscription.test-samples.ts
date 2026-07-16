import dayjs from 'dayjs/esm';

import { ICompanySubscription, NewCompanySubscription } from './company-subscription.model';

export const sampleWithRequiredData: ICompanySubscription = {
  id: 10529,
  type: 'HALF_YEARLY',
  startDate: dayjs('2026-07-16T12:34'),
  endDate: dayjs('2026-07-16T11:53'),
};

export const sampleWithPartialData: ICompanySubscription = {
  id: 16521,
  type: 'MONTHLY',
  startDate: dayjs('2026-07-15T22:01'),
  endDate: dayjs('2026-07-15T23:47'),
  status: 'CANCELLED',
};

export const sampleWithFullData: ICompanySubscription = {
  id: 20120,
  type: 'HALF_YEARLY',
  startDate: dayjs('2026-07-16T17:44'),
  endDate: dayjs('2026-07-16T17:21'),
  status: 'CANCELLED',
};

export const sampleWithNewData: NewCompanySubscription = {
  type: 'QUARTERLY',
  startDate: dayjs('2026-07-16T08:08'),
  endDate: dayjs('2026-07-16T17:02'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
