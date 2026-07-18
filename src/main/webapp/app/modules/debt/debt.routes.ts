import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import DebtResolve from 'app/entities/debt/route/debt-routing-resolve.service';

const debtRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/debt').then(m => m.Debt),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/debt-detail').then(m => m.DebtDetail),
    resolve: {
      debt: DebtResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/debt-update').then(m => m.DebtUpdate),
    resolve: {
      debt: DebtResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/debt-update').then(m => m.DebtUpdate),
    resolve: {
      debt: DebtResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default debtRoute;
