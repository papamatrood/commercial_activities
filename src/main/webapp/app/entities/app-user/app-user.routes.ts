import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import AppUserResolve from './route/app-user-routing-resolve.service';

const appUserRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/app-user').then(m => m.AppUser),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/app-user-detail').then(m => m.AppUserDetail),
    resolve: {
      appUser: AppUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/app-user-update').then(m => m.AppUserUpdate),
    resolve: {
      appUser: AppUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/app-user-update').then(m => m.AppUserUpdate),
    resolve: {
      appUser: AppUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default appUserRoute;
