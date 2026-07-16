import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { StockArrivalService } from '../service/stock-arrival.service';
import { IStockArrival } from '../stock-arrival.model';

const stockArrivalResolve = (route: ActivatedRouteSnapshot): Observable<null | IStockArrival> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(StockArrivalService);
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

export default stockArrivalResolve;
