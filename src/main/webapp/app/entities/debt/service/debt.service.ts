import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IDebt, NewDebt } from '../debt.model';

export type PartialUpdateDebt = Partial<IDebt> & Pick<IDebt, 'id'>;

type RestOf<T extends IDebt | NewDebt> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestDebt = RestOf<IDebt>;

export type NewRestDebt = RestOf<NewDebt>;

export type PartialUpdateRestDebt = RestOf<PartialUpdateDebt>;

@Injectable()
export class DebtsService {
  readonly debtsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly debtsResource = httpResource<RestDebt[]>(() => {
    const params = this.debtsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of debt that have been fetched. It is updated when the debtsResource emits a new value.
   * In case of error while fetching the debts, the signal is set to an empty array.
   */
  readonly debts = computed(() =>
    (this.debtsResource.hasValue() ? this.debtsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/debts');

  protected convertValueFromServer(restDebt: RestDebt): IDebt {
    return {
      ...restDebt,
      date: restDebt.date ? dayjs(restDebt.date) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class DebtService extends DebtsService {
  protected readonly http = inject(HttpClient);

  create(debt: NewDebt): Observable<IDebt> {
    const copy = this.convertValueFromClient(debt);
    return this.http.post<RestDebt>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(debt: IDebt): Observable<IDebt> {
    const copy = this.convertValueFromClient(debt);
    return this.http
      .put<RestDebt>(`${this.resourceUrl}/${encodeURIComponent(this.getDebtIdentifier(debt))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(debt: PartialUpdateDebt): Observable<IDebt> {
    const copy = this.convertValueFromClient(debt);
    return this.http
      .patch<RestDebt>(`${this.resourceUrl}/${encodeURIComponent(this.getDebtIdentifier(debt))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IDebt> {
    return this.http.get<RestDebt>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IDebt[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDebt[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getDebtIdentifier(debt: Pick<IDebt, 'id'>): number {
    return debt.id;
  }

  compareDebt(o1: Pick<IDebt, 'id'> | null, o2: Pick<IDebt, 'id'> | null): boolean {
    return o1 && o2 ? this.getDebtIdentifier(o1) === this.getDebtIdentifier(o2) : o1 === o2;
  }

  addDebtToCollectionIfMissing<Type extends Pick<IDebt, 'id'>>(
    debtCollection: Type[],
    ...debtsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const debts: Type[] = debtsToCheck.filter(isPresent);
    if (debts.length > 0) {
      const debtCollectionIdentifiers = debtCollection.map(debtItem => this.getDebtIdentifier(debtItem));
      const debtsToAdd = debts.filter(debtItem => {
        const debtIdentifier = this.getDebtIdentifier(debtItem);
        if (debtCollectionIdentifiers.includes(debtIdentifier)) {
          return false;
        }
        debtCollectionIdentifiers.push(debtIdentifier);
        return true;
      });
      return [...debtsToAdd, ...debtCollection];
    }
    return debtCollection;
  }

  protected convertValueFromClient<T extends IDebt | NewDebt | PartialUpdateDebt>(debt: T): RestOf<T> {
    return {
      ...debt,
      date: debt.date?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestDebt): IDebt {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestDebt[]): IDebt[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
