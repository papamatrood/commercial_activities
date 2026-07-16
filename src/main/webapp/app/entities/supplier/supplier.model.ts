export interface ISupplier {
  id: number;
  name?: string | null;
  contact?: string | null;
}

export type NewSupplier = Omit<ISupplier, 'id'> & { id: null };
