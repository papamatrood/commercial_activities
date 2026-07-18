import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'commercialActivitiesApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'app-user',
    data: { pageTitle: 'commercialActivitiesApp.appUser.home.title' },
    loadChildren: () => import('./app-user/app-user.routes'),
  },
  {
    path: 'company',
    data: { pageTitle: 'commercialActivitiesApp.company.home.title' },
    loadChildren: () => import('./company/company.routes'),
  },
  {
    path: 'company-subscription',
    data: { pageTitle: 'commercialActivitiesApp.companySubscription.home.title' },
    loadChildren: () => import('./company-subscription/company-subscription.routes'),
  },
  {
    path: 'permission',
    data: { pageTitle: 'commercialActivitiesApp.permission.home.title' },
    loadChildren: () => import('./permission/permission.routes'),
  },
  {
    path: 'supplier',
    data: { pageTitle: 'commercialActivitiesApp.supplier.home.title' },
    loadChildren: () => import('./supplier/supplier.routes'),
  },
  {
    path: 'product',
    data: { pageTitle: 'commercialActivitiesApp.product.home.title' },
    loadChildren: () => import('./product/product.routes'),
  },
  {
    path: 'stock-arrival',
    data: { pageTitle: 'commercialActivitiesApp.stockArrival.home.title' },
    loadChildren: () => import('./stock-arrival/stock-arrival.routes'),
  },
  {
    path: 'sale',
    data: { pageTitle: 'commercialActivitiesApp.sale.home.title' },
    loadChildren: () => import('./sale/sale.routes'),
  },
  {
    path: 'sale-line',
    data: { pageTitle: 'commercialActivitiesApp.saleLine.home.title' },
    loadChildren: () => import('./sale-line/sale-line.routes'),
  },
  {
    path: 'debt',
    data: { pageTitle: 'commercialActivitiesApp.debt.home.title' },
    loadChildren: () => import('./debt/debt.routes'),
  },
  {
    path: 'debt-payment',
    data: { pageTitle: 'commercialActivitiesApp.debtPayment.home.title' },
    loadChildren: () => import('./debt-payment/debt-payment.routes'),
  },
  {
    path: 'cash-collection',
    data: { pageTitle: 'commercialActivitiesApp.cashCollection.home.title' },
    loadChildren: () => import('./cash-collection/cash-collection.routes'),
  },
  {
    path: 'cash-disbursement',
    data: { pageTitle: 'commercialActivitiesApp.cashDisbursement.home.title' },
    loadChildren: () => import('./cash-disbursement/cash-disbursement.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
