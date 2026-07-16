import dayjs from 'dayjs/esm';

import { IAppUser, NewAppUser } from './app-user.model';

export const sampleWithRequiredData: IAppUser = {
  id: 19407,
  phoneNumber: 'conseil d’administration ha adversaire',
  type: 'USER',
};

export const sampleWithPartialData: IAppUser = {
  id: 28648,
  phoneNumber: 'vouh avant de',
  type: 'COMPANY_ADMIN',
  birthDate: dayjs('2026-07-16T19:40'),
  gender: 'MALE',
  disabled: true,
};

export const sampleWithFullData: IAppUser = {
  id: 1902,
  phoneNumber: 'suffisamment',
  type: 'COMPANY_ADMIN',
  birthDate: dayjs('2026-07-16T15:03'),
  birthPlace: 'communiquer insipide',
  gender: 'MALE',
  disabled: false,
  disabledDate: dayjs('2026-07-16T04:35'),
};

export const sampleWithNewData: NewAppUser = {
  phoneNumber: 'distraire juriste jusqu’à ce que',
  type: 'COMPANY_ADMIN',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
