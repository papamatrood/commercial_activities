import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import StockArrivalResolve from 'app/entities/stock-arrival/route/stock-arrival-routing-resolve.service';

const stockArrivalRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/stock-arrival').then(m => m.StockArrival),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/stock-arrival-detail').then(m => m.StockArrivalDetail),
    resolve: {
      stockArrival: StockArrivalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/stock-arrival-update').then(m => m.StockArrivalUpdate),
    resolve: {
      stockArrival: StockArrivalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/stock-arrival-update').then(m => m.StockArrivalUpdate),
    resolve: {
      stockArrival: StockArrivalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default stockArrivalRoute;
