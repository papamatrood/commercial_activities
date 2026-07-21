import type { PermissionKey } from './company-user-permissions.model';

type SidebarNavItem = {
  route: string;
  icon: string;
  translationKey: string;
  defaultLabel: string;
  /**
   * Gates this specific item on a Permission flag for company-scoped users.
   * Ignored (item always shown) for grand admins - see Sidebar.isItemVisible().
   * Omit for items that should be visible to anyone who can see the section.
   */
  permissionKey?: PermissionKey;
};

type SidebarSection = {
  id: string;
  icon: string;
  translationKey: string;
  defaultLabel: string;
  /** Empty/undefined = visible to every authenticated user */
  authorities?: string[];
  items: SidebarNavItem[];
};

export type { SidebarNavItem, SidebarSection };
