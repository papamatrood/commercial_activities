import { Injectable, signal } from '@angular/core';

/**
 * Holds the UI state of the app shell (sidebar collapsed/expanded).
 * Injected by both the topbar (which owns the toggle button) and the
 * main layout / sidebar (which react to the state).
 */
@Injectable({ providedIn: 'root' })
export default class LayoutStateService {
  readonly isSidebarCollapsed = signal(false);

  toggleSidebar(): void {
    this.isSidebarCollapsed.update(collapsed => !collapsed);
  }

  collapseSidebar(): void {
    this.isSidebarCollapsed.set(true);
  }

  expandSidebar(): void {
    this.isSidebarCollapsed.set(false);
  }
}
