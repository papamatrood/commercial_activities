import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPermission, NewPermission } from '../permission.model';

export type PartialUpdatePermission = Partial<IPermission> & Pick<IPermission, 'id'>;

@Injectable()
export class PermissionsService {
  readonly permissionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly permissionsResource = httpResource<IPermission[]>(() => {
    const params = this.permissionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of permission that have been fetched. It is updated when the permissionsResource emits a new value.
   * In case of error while fetching the permissions, the signal is set to an empty array.
   */
  readonly permissions = computed(() => (this.permissionsResource.hasValue() ? this.permissionsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/permissions');
}

@Injectable({ providedIn: 'root' })
export class PermissionService extends PermissionsService {
  protected readonly http = inject(HttpClient);

  create(permission: NewPermission): Observable<IPermission> {
    return this.http.post<IPermission>(this.resourceUrl, permission);
  }

  update(permission: IPermission): Observable<IPermission> {
    return this.http.put<IPermission>(`${this.resourceUrl}/${encodeURIComponent(this.getPermissionIdentifier(permission))}`, permission);
  }

  partialUpdate(permission: PartialUpdatePermission): Observable<IPermission> {
    return this.http.patch<IPermission>(`${this.resourceUrl}/${encodeURIComponent(this.getPermissionIdentifier(permission))}`, permission);
  }

  find(id: number): Observable<IPermission> {
    return this.http.get<IPermission>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPermission[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPermission[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPermissionIdentifier(permission: Pick<IPermission, 'id'>): number {
    return permission.id;
  }

  comparePermission(o1: Pick<IPermission, 'id'> | null, o2: Pick<IPermission, 'id'> | null): boolean {
    return o1 && o2 ? this.getPermissionIdentifier(o1) === this.getPermissionIdentifier(o2) : o1 === o2;
  }

  addPermissionToCollectionIfMissing<Type extends Pick<IPermission, 'id'>>(
    permissionCollection: Type[],
    ...permissionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const permissions: Type[] = permissionsToCheck.filter(isPresent);
    if (permissions.length > 0) {
      const permissionCollectionIdentifiers = permissionCollection.map(permissionItem => this.getPermissionIdentifier(permissionItem));
      const permissionsToAdd = permissions.filter(permissionItem => {
        const permissionIdentifier = this.getPermissionIdentifier(permissionItem);
        if (permissionCollectionIdentifiers.includes(permissionIdentifier)) {
          return false;
        }
        permissionCollectionIdentifiers.push(permissionIdentifier);
        return true;
      });
      return [...permissionsToAdd, ...permissionCollection];
    }
    return permissionCollection;
  }
}
