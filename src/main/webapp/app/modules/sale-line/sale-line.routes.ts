import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import SaleLineResolve from 'app/entities/sale-line/route/sale-line-routing-resolve.service';

const saleLineRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sale-line').then(m => m.SaleLine),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sale-line-detail').then(m => m.SaleLineDetail),
    resolve: {
      saleLine: SaleLineResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sale-line-update').then(m => m.SaleLineUpdate),
    resolve: {
      saleLine: SaleLineResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sale-line-update').then(m => m.SaleLineUpdate),
    resolve: {
      saleLine: SaleLineResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default saleLineRoute;
