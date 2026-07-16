import { ISaleLine, NewSaleLine } from './sale-line.model';

export const sampleWithRequiredData: ISaleLine = {
  id: 18207,
  quantity: 14671,
  unitPrice: 10621.11,
};

export const sampleWithPartialData: ISaleLine = {
  id: 18363,
  quantity: 23045,
  unitPrice: 17635.13,
  totalPrice: 14546.68,
};

export const sampleWithFullData: ISaleLine = {
  id: 26972,
  barcode: 'en guise de par gigantesque',
  quantity: 11147,
  unitPrice: 28057.97,
  totalPrice: 12641.09,
};

export const sampleWithNewData: NewSaleLine = {
  quantity: 13340,
  unitPrice: 22528.92,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
