import dayjs from 'dayjs/esm';

import { IProduct } from 'app/entities/product/product.model';

export interface IStockArrival {
  id: number;
  barcode?: string | null;
  quantity?: number | null;
  amount?: number | null;
  date?: dayjs.Dayjs | null;
  product?: Pick<IProduct, 'id'> | null;
}

export type NewStockArrival = Omit<IStockArrival, 'id'> & { id: null };
