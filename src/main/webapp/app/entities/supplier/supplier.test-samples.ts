import { ISupplier, NewSupplier } from './supplier.model';

export const sampleWithRequiredData: ISupplier = {
  id: 22717,
  name: 'loufoque',
};

export const sampleWithPartialData: ISupplier = {
  id: 30861,
  name: 'sur pauvre',
};

export const sampleWithFullData: ISupplier = {
  id: 3840,
  name: 'mieux au-dessous de',
  contact: 'électorat vlan communauté étudiante',
};

export const sampleWithNewData: NewSupplier = {
  name: 'saigner',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
