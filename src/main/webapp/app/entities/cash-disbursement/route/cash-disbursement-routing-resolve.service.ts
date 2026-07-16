import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICashDisbursement } from '../cash-disbursement.model';
import { CashDisbursementService } from '../service/cash-disbursement.service';

const cashDisbursementResolve = (route: ActivatedRouteSnapshot): Observable<null | ICashDisbursement> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CashDisbursementService);
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

export default cashDisbursementResolve;
