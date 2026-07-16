import dayjs from 'dayjs/esm';

import { ICompany, NewCompany } from './company.model';

export const sampleWithRequiredData: ICompany = {
  id: 6800,
  name: 'regarder crac',
};

export const sampleWithPartialData: ICompany = {
  id: 25647,
  name: 'équipe',
  location: 'pin-pon de manière à ce que pis',
  status: 'ACTIVE',
};

export const sampleWithFullData: ICompany = {
  id: 14111,
  name: 'brusque',
  location: 'à cause de',
  creationDate: dayjs('2026-07-16T14:21'),
  status: 'BLOCKED',
};

export const sampleWithNewData: NewCompany = {
  name: "à l'entour de exploser",
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
