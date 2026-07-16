import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IAppUser } from '../app-user.model';
import { AppUserService } from '../service/app-user.service';

const appUserResolve = (route: ActivatedRouteSnapshot): Observable<null | IAppUser> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(AppUserService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default appUserResolve;
