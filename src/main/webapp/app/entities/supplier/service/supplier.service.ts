import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ISupplier, NewSupplier } from '../supplier.model';

export type PartialUpdateSupplier = Partial<ISupplier> & Pick<ISupplier, 'id'>;

@Injectable()
export class SuppliersService {
  readonly suppliersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly suppliersResource = httpResource<ISupplier[]>(() => {
    const params = this.suppliersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of supplier that have been fetched. It is updated when the suppliersResource emits a new value.
   * In case of error while fetching the suppliers, the signal is set to an empty array.
   */
  readonly suppliers = computed(() => (this.suppliersResource.hasValue() ? this.suppliersResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/suppliers');
}

@Injectable({ providedIn: 'root' })
export class SupplierService extends SuppliersService {
  protected readonly http = inject(HttpClient);

  create(supplier: NewSupplier): Observable<ISupplier> {
    return this.http.post<ISupplier>(this.resourceUrl, supplier);
  }

  update(supplier: ISupplier): Observable<ISupplier> {
    return this.http.put<ISupplier>(`${this.resourceUrl}/${encodeURIComponent(this.getSupplierIdentifier(supplier))}`, supplier);
  }

  partialUpdate(supplier: PartialUpdateSupplier): Observable<ISupplier> {
    return this.http.patch<ISupplier>(`${this.resourceUrl}/${encodeURIComponent(this.getSupplierIdentifier(supplier))}`, supplier);
  }

  find(id: number): Observable<ISupplier> {
    return this.http.get<ISupplier>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ISupplier[]>> {
    const options = createRequestOption(req);
    return this.http.get<ISupplier[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getSupplierIdentifier(supplier: Pick<ISupplier, 'id'>): number {
    return supplier.id;
  }

  compareSupplier(o1: Pick<ISupplier, 'id'> | null, o2: Pick<ISupplier, 'id'> | null): boolean {
    return o1 && o2 ? this.getSupplierIdentifier(o1) === this.getSupplierIdentifier(o2) : o1 === o2;
  }

  addSupplierToCollectionIfMissing<Type extends Pick<ISupplier, 'id'>>(
    supplierCollection: Type[],
    ...suppliersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const suppliers: Type[] = suppliersToCheck.filter(isPresent);
    if (suppliers.length > 0) {
      const supplierCollectionIdentifiers = supplierCollection.map(supplierItem => this.getSupplierIdentifier(supplierItem));
      const suppliersToAdd = suppliers.filter(supplierItem => {
        const supplierIdentifier = this.getSupplierIdentifier(supplierItem);
        if (supplierCollectionIdentifiers.includes(supplierIdentifier)) {
          return false;
        }
        supplierCollectionIdentifiers.push(supplierIdentifier);
        return true;
      });
      return [...suppliersToAdd, ...supplierCollection];
    }
    return supplierCollection;
  }
}
