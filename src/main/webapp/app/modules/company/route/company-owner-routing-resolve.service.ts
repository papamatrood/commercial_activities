import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICompanyWithOwner } from '../company-owner.model';
import { CompanyOwnerService } from '../company-owner.service';

const companyOwnerResolve = (route: ActivatedRouteSnapshot): Observable<null | ICompanyWithOwner> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CompanyOwnerService);
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

export default companyOwnerResolve;
