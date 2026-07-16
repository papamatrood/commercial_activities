import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ISaleLine, NewSaleLine } from '../sale-line.model';

export type PartialUpdateSaleLine = Partial<ISaleLine> & Pick<ISaleLine, 'id'>;

@Injectable()
export class SaleLinesService {
  readonly saleLinesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly saleLinesResource = httpResource<ISaleLine[]>(() => {
    const params = this.saleLinesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of saleLine that have been fetched. It is updated when the saleLinesResource emits a new value.
   * In case of error while fetching the saleLines, the signal is set to an empty array.
   */
  readonly saleLines = computed(() => (this.saleLinesResource.hasValue() ? this.saleLinesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/sale-lines');
}

@Injectable({ providedIn: 'root' })
export class SaleLineService extends SaleLinesService {
  protected readonly http = inject(HttpClient);

  create(saleLine: NewSaleLine): Observable<ISaleLine> {
    return this.http.post<ISaleLine>(this.resourceUrl, saleLine);
  }

  update(saleLine: ISaleLine): Observable<ISaleLine> {
    return this.http.put<ISaleLine>(`${this.resourceUrl}/${encodeURIComponent(this.getSaleLineIdentifier(saleLine))}`, saleLine);
  }

  partialUpdate(saleLine: PartialUpdateSaleLine): Observable<ISaleLine> {
    return this.http.patch<ISaleLine>(`${this.resourceUrl}/${encodeURIComponent(this.getSaleLineIdentifier(saleLine))}`, saleLine);
  }

  find(id: number): Observable<ISaleLine> {
    return this.http.get<ISaleLine>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ISaleLine[]>> {
    const options = createRequestOption(req);
    return this.http.get<ISaleLine[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getSaleLineIdentifier(saleLine: Pick<ISaleLine, 'id'>): number {
    return saleLine.id;
  }

  compareSaleLine(o1: Pick<ISaleLine, 'id'> | null, o2: Pick<ISaleLine, 'id'> | null): boolean {
    return o1 && o2 ? this.getSaleLineIdentifier(o1) === this.getSaleLineIdentifier(o2) : o1 === o2;
  }

  addSaleLineToCollectionIfMissing<Type extends Pick<ISaleLine, 'id'>>(
    saleLineCollection: Type[],
    ...saleLinesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const saleLines: Type[] = saleLinesToCheck.filter(isPresent);
    if (saleLines.length > 0) {
      const saleLineCollectionIdentifiers = saleLineCollection.map(saleLineItem => this.getSaleLineIdentifier(saleLineItem));
      const saleLinesToAdd = saleLines.filter(saleLineItem => {
        const saleLineIdentifier = this.getSaleLineIdentifier(saleLineItem);
        if (saleLineCollectionIdentifiers.includes(saleLineIdentifier)) {
          return false;
        }
        saleLineCollectionIdentifiers.push(saleLineIdentifier);
        return true;
      });
      return [...saleLinesToAdd, ...saleLineCollection];
    }
    return saleLineCollection;
  }
}
