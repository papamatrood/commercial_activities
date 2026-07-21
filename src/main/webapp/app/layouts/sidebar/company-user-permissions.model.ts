/**
 * Permission flags for a *company-scoped* user (simple user or company admin).
 * A company admin is just a user whose Permission record has every flag
 * set to true ("tous les droits d'administrateur d'entreprise") - there is
 * no separate Spring authority for it, this same interface covers both.
 *
 * Grand admins (ROLE_ADMIN) are never scoped by this - they are gated at
 * the section level via `authorities: ['ROLE_ADMIN']` instead.
 */
export interface CompanyUserPermissions {
  /** Enregistrement d'un utilisateur d'entreprise + gestion de ses droits */
  canManageUsers: boolean;
  /** Consulter/gérer les infos de sa propre entreprise et son abonnement */
  canManageCompany: boolean;
  /** Enregistrement de produits, fournisseurs, arrivages de stock */
  canManageProducts: boolean;
  /** Enregistrement des ventes, dettes et paiements de dettes */
  canRegisterSales: boolean;
  /** Enregistrement du montant journalier de la caisse (encaissement) */
  canManageCashCollection: boolean;
  /** Enregistrement d'un décaissement */
  canManageCashDisbursement: boolean;
  /** Visualisation des rapports (ventes, stock, états financiers) */
  canViewReports: boolean;
  /** Visualisation du tableau de bord */
  canViewDashboard: boolean;
}

export type PermissionKey = keyof CompanyUserPermissions;

export const EMPTY_PERMISSIONS: CompanyUserPermissions = {
  canManageUsers: false,
  canManageCompany: false,
  canManageProducts: false,
  canRegisterSales: false,
  canManageCashCollection: false,
  canManageCashDisbursement: false,
  canViewReports: false,
  canViewDashboard: false,
};
