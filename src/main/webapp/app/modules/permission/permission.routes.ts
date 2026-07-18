import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PermissionResolve from 'app/entities/permission/route/permission-routing-resolve.service';

const permissionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/permission').then(m => m.Permission),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/permission-detail').then(m => m.PermissionDetail),
    resolve: {
      permission: PermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/permission-update').then(m => m.PermissionUpdate),
    resolve: {
      permission: PermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/permission-update').then(m => m.PermissionUpdate),
    resolve: {
      permission: PermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default permissionRoute;
