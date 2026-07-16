import { ICompany } from 'app/entities/company/company.model';
import { ISupplier } from 'app/entities/supplier/supplier.model';

export interface IProduct {
  id: number;
  barcode?: string | null;
  designation?: string | null;
  initialStock?: number | null;
  currentStock?: number | null;
  unitPrice?: number | null;
  company?: Pick<ICompany, 'id'> | null;
  supplier?: Pick<ISupplier, 'id'> | null;
}

export type NewProduct = Omit<IProduct, 'id'> & { id: null };
