import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICompanySubscription } from '../company-subscription.model';
import { CompanySubscriptionService } from '../service/company-subscription.service';

const companySubscriptionResolve = (route: ActivatedRouteSnapshot): Observable<null | ICompanySubscription> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CompanySubscriptionService);
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

export default companySubscriptionResolve;
