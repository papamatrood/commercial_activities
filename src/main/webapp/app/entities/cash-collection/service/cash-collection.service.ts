import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICashCollection, NewCashCollection } from '../cash-collection.model';

export type PartialUpdateCashCollection = Partial<ICashCollection> & Pick<ICashCollection, 'id'>;

type RestOf<T extends ICashCollection | NewCashCollection> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestCashCollection = RestOf<ICashCollection>;

export type NewRestCashCollection = RestOf<NewCashCollection>;

export type PartialUpdateRestCashCollection = RestOf<PartialUpdateCashCollection>;

@Injectable()
export class CashCollectionsService {
  readonly cashCollectionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly cashCollectionsResource = httpResource<RestCashCollection[]>(() => {
    const params = this.cashCollectionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of cashCollection that have been fetched. It is updated when the cashCollectionsResource emits a new value.
   * In case of error while fetching the cashCollections, the signal is set to an empty array.
   */
  readonly cashCollections = computed(() =>
    (this.cashCollectionsResource.hasValue() ? this.cashCollectionsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/cash-collections');

  protected convertValueFromServer(restCashCollection: RestCashCollection): ICashCollection {
    return {
      ...restCashCollection,
      date: restCashCollection.date ? dayjs(restCashCollection.date) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CashCollectionService extends CashCollectionsService {
  protected readonly http = inject(HttpClient);

  create(cashCollection: NewCashCollection): Observable<ICashCollection> {
    const copy = this.convertValueFromClient(cashCollection);
    return this.http.post<RestCashCollection>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(cashCollection: ICashCollection): Observable<ICashCollection> {
    const copy = this.convertValueFromClient(cashCollection);
    return this.http
      .put<RestCashCollection>(`${this.resourceUrl}/${encodeURIComponent(this.getCashCollectionIdentifier(cashCollection))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(cashCollection: PartialUpdateCashCollection): Observable<ICashCollection> {
    const copy = this.convertValueFromClient(cashCollection);
    return this.http
      .patch<RestCashCollection>(`${this.resourceUrl}/${encodeURIComponent(this.getCashCollectionIdentifier(cashCollection))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICashCollection> {
    return this.http
      .get<RestCashCollection>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICashCollection[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCashCollection[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCashCollectionIdentifier(cashCollection: Pick<ICashCollection, 'id'>): number {
    return cashCollection.id;
  }

  compareCashCollection(o1: Pick<ICashCollection, 'id'> | null, o2: Pick<ICashCollection, 'id'> | null): boolean {
    return o1 && o2 ? this.getCashCollectionIdentifier(o1) === this.getCashCollectionIdentifier(o2) : o1 === o2;
  }

  addCashCollectionToCollectionIfMissing<Type extends Pick<ICashCollection, 'id'>>(
    cashCollectionCollection: Type[],
    ...cashCollectionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cashCollections: Type[] = cashCollectionsToCheck.filter(isPresent);
    if (cashCollections.length > 0) {
      const cashCollectionCollectionIdentifiers = cashCollectionCollection.map(cashCollectionItem =>
        this.getCashCollectionIdentifier(cashCollectionItem),
      );
      const cashCollectionsToAdd = cashCollections.filter(cashCollectionItem => {
        const cashCollectionIdentifier = this.getCashCollectionIdentifier(cashCollectionItem);
        if (cashCollectionCollectionIdentifiers.includes(cashCollectionIdentifier)) {
          return false;
        }
        cashCollectionCollectionIdentifiers.push(cashCollectionIdentifier);
        return true;
      });
      return [...cashCollectionsToAdd, ...cashCollectionCollection];
    }
    return cashCollectionCollection;
  }

  protected convertValueFromClient<T extends ICashCollection | NewCashCollection | PartialUpdateCashCollection>(
    cashCollection: T,
  ): RestOf<T> {
    return {
      ...cashCollection,
      date: cashCollection.date?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCashCollection): ICashCollection {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCashCollection[]): ICashCollection[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
