import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import DebtPaymentResolve from 'app/entities/debt-payment/route/debt-payment-routing-resolve.service';

const debtPaymentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/debt-payment').then(m => m.DebtPayment),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/debt-payment-detail').then(m => m.DebtPaymentDetail),
    resolve: {
      debtPayment: DebtPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/debt-payment-update').then(m => m.DebtPaymentUpdate),
    resolve: {
      debtPayment: DebtPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/debt-payment-update').then(m => m.DebtPaymentUpdate),
    resolve: {
      debtPayment: DebtPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default debtPaymentRoute;
