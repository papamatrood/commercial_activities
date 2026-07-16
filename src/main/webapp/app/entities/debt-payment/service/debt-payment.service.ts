import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IDebtPayment, NewDebtPayment } from '../debt-payment.model';

export type PartialUpdateDebtPayment = Partial<IDebtPayment> & Pick<IDebtPayment, 'id'>;

type RestOf<T extends IDebtPayment | NewDebtPayment> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestDebtPayment = RestOf<IDebtPayment>;

export type NewRestDebtPayment = RestOf<NewDebtPayment>;

export type PartialUpdateRestDebtPayment = RestOf<PartialUpdateDebtPayment>;

@Injectable()
export class DebtPaymentsService {
  readonly debtPaymentsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly debtPaymentsResource = httpResource<RestDebtPayment[]>(() => {
    const params = this.debtPaymentsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of debtPayment that have been fetched. It is updated when the debtPaymentsResource emits a new value.
   * In case of error while fetching the debtPayments, the signal is set to an empty array.
   */
  readonly debtPayments = computed(() =>
    (this.debtPaymentsResource.hasValue() ? this.debtPaymentsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/debt-payments');

  protected convertValueFromServer(restDebtPayment: RestDebtPayment): IDebtPayment {
    return {
      ...restDebtPayment,
      date: restDebtPayment.date ? dayjs(restDebtPayment.date) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class DebtPaymentService extends DebtPaymentsService {
  protected readonly http = inject(HttpClient);

  create(debtPayment: NewDebtPayment): Observable<IDebtPayment> {
    const copy = this.convertValueFromClient(debtPayment);
    return this.http.post<RestDebtPayment>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(debtPayment: IDebtPayment): Observable<IDebtPayment> {
    const copy = this.convertValueFromClient(debtPayment);
    return this.http
      .put<RestDebtPayment>(`${this.resourceUrl}/${encodeURIComponent(this.getDebtPaymentIdentifier(debtPayment))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(debtPayment: PartialUpdateDebtPayment): Observable<IDebtPayment> {
    const copy = this.convertValueFromClient(debtPayment);
    return this.http
      .patch<RestDebtPayment>(`${this.resourceUrl}/${encodeURIComponent(this.getDebtPaymentIdentifier(debtPayment))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IDebtPayment> {
    return this.http
      .get<RestDebtPayment>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IDebtPayment[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDebtPayment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getDebtPaymentIdentifier(debtPayment: Pick<IDebtPayment, 'id'>): number {
    return debtPayment.id;
  }

  compareDebtPayment(o1: Pick<IDebtPayment, 'id'> | null, o2: Pick<IDebtPayment, 'id'> | null): boolean {
    return o1 && o2 ? this.getDebtPaymentIdentifier(o1) === this.getDebtPaymentIdentifier(o2) : o1 === o2;
  }

  addDebtPaymentToCollectionIfMissing<Type extends Pick<IDebtPayment, 'id'>>(
    debtPaymentCollection: Type[],
    ...debtPaymentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const debtPayments: Type[] = debtPaymentsToCheck.filter(isPresent);
    if (debtPayments.length > 0) {
      const debtPaymentCollectionIdentifiers = debtPaymentCollection.map(debtPaymentItem => this.getDebtPaymentIdentifier(debtPaymentItem));
      const debtPaymentsToAdd = debtPayments.filter(debtPaymentItem => {
        const debtPaymentIdentifier = this.getDebtPaymentIdentifier(debtPaymentItem);
        if (debtPaymentCollectionIdentifiers.includes(debtPaymentIdentifier)) {
          return false;
        }
        debtPaymentCollectionIdentifiers.push(debtPaymentIdentifier);
        return true;
      });
      return [...debtPaymentsToAdd, ...debtPaymentCollection];
    }
    return debtPaymentCollection;
  }

  protected convertValueFromClient<T extends IDebtPayment | NewDebtPayment | PartialUpdateDebtPayment>(debtPayment: T): RestOf<T> {
    return {
      ...debtPayment,
      date: debtPayment.date?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestDebtPayment): IDebtPayment {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestDebtPayment[]): IDebtPayment[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
