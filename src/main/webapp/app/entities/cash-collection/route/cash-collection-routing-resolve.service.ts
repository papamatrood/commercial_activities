import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICashCollection } from '../cash-collection.model';
import { CashCollectionService } from '../service/cash-collection.service';

const cashCollectionResolve = (route: ActivatedRouteSnapshot): Observable<null | ICashCollection> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CashCollectionService);
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

export default cashCollectionResolve;
