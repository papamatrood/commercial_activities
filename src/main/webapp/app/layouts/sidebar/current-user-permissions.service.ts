import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Authority } from '../../shared/jhipster/constants';

import { EMPTY_PERMISSIONS, type CompanyUserPermissions } from './company-user-permissions.model';

/**
 * NB: `resourceUrl` is a best-effort default. Point it at whatever endpoint
 * returns the Permission flags for the *currently authenticated* app-user
 * (e.g. a small custom `/api/account/permissions` controller, or
 * `/api/permissions?appUserId.equals={id}` if you'd rather reuse the
 * generated Permission CRUD resource and take the first result).
 */
@Injectable({ providedIn: 'root' })
export default class CurrentUserPermissionsService {
  readonly permissions = signal<CompanyUserPermissions>(EMPTY_PERMISSIONS);

  private readonly resourceUrl = 'api/account/permissions';
  private readonly http = inject(HttpClient);
  private readonly accountService = inject(AccountService);

  constructor() {
    toObservable(this.accountService.account)
      .pipe(
        switchMap(account => {
          if (!account) {
            return of(EMPTY_PERMISSIONS);
          }
          if (account.authorities?.includes(Authority.ADMIN)) {
            // Grand admins aren't scoped by the Permission entity at all -
            // sidebar visibility for them is handled via isGrandAdmin() instead.
            return of(EMPTY_PERMISSIONS);
          }
          return this.http.get<CompanyUserPermissions>(this.resourceUrl);
        }),
      )
      .subscribe({
        next: permissions => this.permissions.set(permissions ?? EMPTY_PERMISSIONS),
        error: () => this.permissions.set(EMPTY_PERMISSIONS),
      });
  }
}
