import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICashDisbursement, NewCashDisbursement } from '../cash-disbursement.model';

export type PartialUpdateCashDisbursement = Partial<ICashDisbursement> & Pick<ICashDisbursement, 'id'>;

type RestOf<T extends ICashDisbursement | NewCashDisbursement> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestCashDisbursement = RestOf<ICashDisbursement>;

export type NewRestCashDisbursement = RestOf<NewCashDisbursement>;

export type PartialUpdateRestCashDisbursement = RestOf<PartialUpdateCashDisbursement>;

@Injectable()
export class CashDisbursementsService {
  readonly cashDisbursementsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly cashDisbursementsResource = httpResource<RestCashDisbursement[]>(() => {
    const params = this.cashDisbursementsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of cashDisbursement that have been fetched. It is updated when the cashDisbursementsResource emits a new value.
   * In case of error while fetching the cashDisbursements, the signal is set to an empty array.
   */
  readonly cashDisbursements = computed(() =>
    (this.cashDisbursementsResource.hasValue() ? this.cashDisbursementsResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/cash-disbursements');

  protected convertValueFromServer(restCashDisbursement: RestCashDisbursement): ICashDisbursement {
    return {
      ...restCashDisbursement,
      date: restCashDisbursement.date ? dayjs(restCashDisbursement.date) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CashDisbursementService extends CashDisbursementsService {
  protected readonly http = inject(HttpClient);

  create(cashDisbursement: NewCashDisbursement): Observable<ICashDisbursement> {
    const copy = this.convertValueFromClient(cashDisbursement);
    return this.http.post<RestCashDisbursement>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(cashDisbursement: ICashDisbursement): Observable<ICashDisbursement> {
    const copy = this.convertValueFromClient(cashDisbursement);
    return this.http
      .put<RestCashDisbursement>(`${this.resourceUrl}/${encodeURIComponent(this.getCashDisbursementIdentifier(cashDisbursement))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(cashDisbursement: PartialUpdateCashDisbursement): Observable<ICashDisbursement> {
    const copy = this.convertValueFromClient(cashDisbursement);
    return this.http
      .patch<RestCashDisbursement>(`${this.resourceUrl}/${encodeURIComponent(this.getCashDisbursementIdentifier(cashDisbursement))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICashDisbursement> {
    return this.http
      .get<RestCashDisbursement>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICashDisbursement[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCashDisbursement[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCashDisbursementIdentifier(cashDisbursement: Pick<ICashDisbursement, 'id'>): number {
    return cashDisbursement.id;
  }

  compareCashDisbursement(o1: Pick<ICashDisbursement, 'id'> | null, o2: Pick<ICashDisbursement, 'id'> | null): boolean {
    return o1 && o2 ? this.getCashDisbursementIdentifier(o1) === this.getCashDisbursementIdentifier(o2) : o1 === o2;
  }

  addCashDisbursementToCollectionIfMissing<Type extends Pick<ICashDisbursement, 'id'>>(
    cashDisbursementCollection: Type[],
    ...cashDisbursementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cashDisbursements: Type[] = cashDisbursementsToCheck.filter(isPresent);
    if (cashDisbursements.length > 0) {
      const cashDisbursementCollectionIdentifiers = cashDisbursementCollection.map(cashDisbursementItem =>
        this.getCashDisbursementIdentifier(cashDisbursementItem),
      );
      const cashDisbursementsToAdd = cashDisbursements.filter(cashDisbursementItem => {
        const cashDisbursementIdentifier = this.getCashDisbursementIdentifier(cashDisbursementItem);
        if (cashDisbursementCollectionIdentifiers.includes(cashDisbursementIdentifier)) {
          return false;
        }
        cashDisbursementCollectionIdentifiers.push(cashDisbursementIdentifier);
        return true;
      });
      return [...cashDisbursementsToAdd, ...cashDisbursementCollection];
    }
    return cashDisbursementCollection;
  }

  protected convertValueFromClient<T extends ICashDisbursement | NewCashDisbursement | PartialUpdateCashDisbursement>(
    cashDisbursement: T,
  ): RestOf<T> {
    return {
      ...cashDisbursement,
      date: cashDisbursement.date?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCashDisbursement): ICashDisbursement {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCashDisbursement[]): ICashDisbursement[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
