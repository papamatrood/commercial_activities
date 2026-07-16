import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IDebtPayment } from '../debt-payment.model';
import { DebtPaymentService } from '../service/debt-payment.service';

const debtPaymentResolve = (route: ActivatedRouteSnapshot): Observable<null | IDebtPayment> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(DebtPaymentService);
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

export default debtPaymentResolve;
