import { IProduct } from 'app/entities/product/product.model';
import { ISale } from 'app/entities/sale/sale.model';

export interface ISaleLine {
  id: number;
  barcode?: string | null;
  quantity?: number | null;
  unitPrice?: number | null;
  totalPrice?: number | null;
  sale?: Pick<ISale, 'id'> | null;
  product?: Pick<IProduct, 'id'> | null;
}

export type NewSaleLine = Omit<ISaleLine, 'id'> & { id: null };
