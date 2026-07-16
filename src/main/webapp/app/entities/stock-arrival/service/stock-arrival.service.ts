import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IStockArrival, NewStockArrival } from '../stock-arrival.model';

export type PartialUpdateStockArrival = Partial<IStockArrival> & Pick<IStockArrival, 'id'>;

type RestOf<T extends IStockArrival | NewStockArrival> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestStockArrival = RestOf<IStockArrival>;

export type NewRestStockArrival = RestOf<NewStockArrival>;

export type PartialUpdateRestStockArrival = RestOf<PartialUpdateStockArrival>;

@Injectable()
export class StockArrivalsService {
  readonly stockArrivalsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly stockArrivalsResource = httpResource<RestStockArrival[]>(() => {
    const params = this.stockArrivalsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of stockArrival that have been fetched. It is updated when the stockArrivalsResource emits a new value.
   * In case of error while fetching the stockArrivals, the signal is set to an empty array.
   */
  readonly stockArrivals = computed(() =>
    (this.stockArrivalsResource.hasValue() ? this.stockArrivalsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/stock-arrivals');

  protected convertValueFromServer(restStockArrival: RestStockArrival): IStockArrival {
    return {
      ...restStockArrival,
      date: restStockArrival.date ? dayjs(restStockArrival.date) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class StockArrivalService extends StockArrivalsService {
  protected readonly http = inject(HttpClient);

  create(stockArrival: NewStockArrival): Observable<IStockArrival> {
    const copy = this.convertValueFromClient(stockArrival);
    return this.http.post<RestStockArrival>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(stockArrival: IStockArrival): Observable<IStockArrival> {
    const copy = this.convertValueFromClient(stockArrival);
    return this.http
      .put<RestStockArrival>(`${this.resourceUrl}/${encodeURIComponent(this.getStockArrivalIdentifier(stockArrival))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(stockArrival: PartialUpdateStockArrival): Observable<IStockArrival> {
    const copy = this.convertValueFromClient(stockArrival);
    return this.http
      .patch<RestStockArrival>(`${this.resourceUrl}/${encodeURIComponent(this.getStockArrivalIdentifier(stockArrival))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IStockArrival> {
    return this.http
      .get<RestStockArrival>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IStockArrival[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStockArrival[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getStockArrivalIdentifier(stockArrival: Pick<IStockArrival, 'id'>): number {
    return stockArrival.id;
  }

  compareStockArrival(o1: Pick<IStockArrival, 'id'> | null, o2: Pick<IStockArrival, 'id'> | null): boolean {
    return o1 && o2 ? this.getStockArrivalIdentifier(o1) === this.getStockArrivalIdentifier(o2) : o1 === o2;
  }

  addStockArrivalToCollectionIfMissing<Type extends Pick<IStockArrival, 'id'>>(
    stockArrivalCollection: Type[],
    ...stockArrivalsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const stockArrivals: Type[] = stockArrivalsToCheck.filter(isPresent);
    if (stockArrivals.length > 0) {
      const stockArrivalCollectionIdentifiers = stockArrivalCollection.map(stockArrivalItem =>
        this.getStockArrivalIdentifier(stockArrivalItem),
      );
      const stockArrivalsToAdd = stockArrivals.filter(stockArrivalItem => {
        const stockArrivalIdentifier = this.getStockArrivalIdentifier(stockArrivalItem);
        if (stockArrivalCollectionIdentifiers.includes(stockArrivalIdentifier)) {
          return false;
        }
        stockArrivalCollectionIdentifiers.push(stockArrivalIdentifier);
        return true;
      });
      return [...stockArrivalsToAdd, ...stockArrivalCollection];
    }
    return stockArrivalCollection;
  }

  protected convertValueFromClient<T extends IStockArrival | NewStockArrival | PartialUpdateStockArrival>(stockArrival: T): RestOf<T> {
    return {
      ...stockArrival,
      date: stockArrival.date?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestStockArrival): IStockArrival {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestStockArrival[]): IStockArrival[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
