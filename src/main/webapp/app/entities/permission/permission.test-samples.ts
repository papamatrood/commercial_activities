import { IPermission, NewPermission } from './permission.model';

export const sampleWithRequiredData: IPermission = {
  id: 8197,
  code: "à l'entour de moderne hypocrite",
  label: 'raide',
};

export const sampleWithPartialData: IPermission = {
  id: 20587,
  code: 'du fait que',
  label: 'collègue toujours',
};

export const sampleWithFullData: IPermission = {
  id: 11619,
  code: 'sauf cuicui trop peu',
  label: 'nonobstant vétuste à partir de',
};

export const sampleWithNewData: NewPermission = {
  code: 'trop si grrr',
  label: 'direction',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
