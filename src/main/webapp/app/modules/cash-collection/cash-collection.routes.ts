import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CashCollectionResolve from 'app/entities/cash-collection/route/cash-collection-routing-resolve.service';

const cashCollectionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/cash-collection').then(m => m.CashCollection),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/cash-collection-detail').then(m => m.CashCollectionDetail),
    resolve: {
      cashCollection: CashCollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/cash-collection-update').then(m => m.CashCollectionUpdate),
    resolve: {
      cashCollection: CashCollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/cash-collection-update').then(m => m.CashCollectionUpdate),
    resolve: {
      cashCollection: CashCollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cashCollectionRoute;
