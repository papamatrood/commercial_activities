import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CashDisbursementResolve from './route/cash-disbursement-routing-resolve.service';

const cashDisbursementRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/cash-disbursement').then(m => m.CashDisbursement),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/cash-disbursement-detail').then(m => m.CashDisbursementDetail),
    resolve: {
      cashDisbursement: CashDisbursementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/cash-disbursement-update').then(m => m.CashDisbursementUpdate),
    resolve: {
      cashDisbursement: CashDisbursementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/cash-disbursement-update').then(m => m.CashDisbursementUpdate),
    resolve: {
      cashDisbursement: CashDisbursementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cashDisbursementRoute;
