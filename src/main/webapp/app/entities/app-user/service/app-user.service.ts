import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IAppUser, NewAppUser } from '../app-user.model';

export type PartialUpdateAppUser = Partial<IAppUser> & Pick<IAppUser, 'id'>;

type RestOf<T extends IAppUser | NewAppUser> = Omit<T, 'birthDate' | 'disabledDate'> & {
  birthDate?: string | null;
  disabledDate?: string | null;
};

export type RestAppUser = RestOf<IAppUser>;

export type NewRestAppUser = RestOf<NewAppUser>;

export type PartialUpdateRestAppUser = RestOf<PartialUpdateAppUser>;

@Injectable()
export class AppUsersService {
  readonly appUsersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly appUsersResource = httpResource<RestAppUser[]>(() => {
    const params = this.appUsersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of appUser that have been fetched. It is updated when the appUsersResource emits a new value.
   * In case of error while fetching the appUsers, the signal is set to an empty array.
   */
  readonly appUsers = computed(() =>
    (this.appUsersResource.hasValue() ? this.appUsersResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/app-users');

  protected convertValueFromServer(restAppUser: RestAppUser): IAppUser {
    return {
      ...restAppUser,
      birthDate: restAppUser.birthDate ? dayjs(restAppUser.birthDate) : undefined,
      disabledDate: restAppUser.disabledDate ? dayjs(restAppUser.disabledDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class AppUserService extends AppUsersService {
  protected readonly http = inject(HttpClient);

  create(appUser: NewAppUser): Observable<IAppUser> {
    const copy = this.convertValueFromClient(appUser);
    return this.http.post<RestAppUser>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(appUser: IAppUser): Observable<IAppUser> {
    const copy = this.convertValueFromClient(appUser);
    return this.http
      .put<RestAppUser>(`${this.resourceUrl}/${encodeURIComponent(this.getAppUserIdentifier(appUser))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(appUser: PartialUpdateAppUser): Observable<IAppUser> {
    const copy = this.convertValueFromClient(appUser);
    return this.http
      .patch<RestAppUser>(`${this.resourceUrl}/${encodeURIComponent(this.getAppUserIdentifier(appUser))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IAppUser> {
    return this.http
      .get<RestAppUser>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IAppUser[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAppUser[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAppUserIdentifier(appUser: Pick<IAppUser, 'id'>): number {
    return appUser.id;
  }

  compareAppUser(o1: Pick<IAppUser, 'id'> | null, o2: Pick<IAppUser, 'id'> | null): boolean {
    return o1 && o2 ? this.getAppUserIdentifier(o1) === this.getAppUserIdentifier(o2) : o1 === o2;
  }

  addAppUserToCollectionIfMissing<Type extends Pick<IAppUser, 'id'>>(
    appUserCollection: Type[],
    ...appUsersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const appUsers: Type[] = appUsersToCheck.filter(isPresent);
    if (appUsers.length > 0) {
      const appUserCollectionIdentifiers = appUserCollection.map(appUserItem => this.getAppUserIdentifier(appUserItem));
      const appUsersToAdd = appUsers.filter(appUserItem => {
        const appUserIdentifier = this.getAppUserIdentifier(appUserItem);
        if (appUserCollectionIdentifiers.includes(appUserIdentifier)) {
          return false;
        }
        appUserCollectionIdentifiers.push(appUserIdentifier);
        return true;
      });
      return [...appUsersToAdd, ...appUserCollection];
    }
    return appUserCollection;
  }

  protected convertValueFromClient<T extends IAppUser | NewAppUser | PartialUpdateAppUser>(appUser: T): RestOf<T> {
    return {
      ...appUser,
      birthDate: appUser.birthDate?.toJSON() ?? null,
      disabledDate: appUser.disabledDate?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestAppUser): IAppUser {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestAppUser[]): IAppUser[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
