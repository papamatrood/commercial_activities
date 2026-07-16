import { IProduct, NewProduct } from './product.model';

export const sampleWithRequiredData: IProduct = {
  id: 11737,
  barcode: 'drelin au-dessous',
  designation: 'oh alors que',
  unitPrice: 16146.92,
};

export const sampleWithPartialData: IProduct = {
  id: 22728,
  barcode: 'sédentaire psitt terne',
  designation: 'devenir',
  currentStock: 8788,
  unitPrice: 30762.17,
};

export const sampleWithFullData: IProduct = {
  id: 4403,
  barcode: 'actionnaire',
  designation: 'depuis en',
  initialStock: 12918,
  currentStock: 110,
  unitPrice: 32035.31,
};

export const sampleWithNewData: NewProduct = {
  barcode: 'hormis fréquenter tandis que',
  designation: 'envers magnifique hors',
  unitPrice: 7532.66,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
