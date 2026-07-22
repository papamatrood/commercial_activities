import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CompanyResolve from 'app/entities/company/route/company-routing-resolve.service';

import CompanyOwnerResolve from './route/company-owner-routing-resolve.service';

const companyRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/company').then(m => m.Company),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/company-detail').then(m => m.CompanyDetail),
    resolve: {
      company: CompanyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/company-update').then(m => m.CompanyUpdate),
    resolve: {
      companyWithOwner: CompanyOwnerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/company-update').then(m => m.CompanyUpdate),
    resolve: {
      companyWithOwner: CompanyOwnerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default companyRoute;
