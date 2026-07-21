import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth/account.service';
import { Authority } from '../../shared/jhipster/constants';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { TranslateDirective } from 'app/shared/language';

import LayoutStateService from '../layout-state.service';
import CurrentUserPermissionsService from './current-user-permissions.service';
import type { SidebarNavItem } from './sidebar-item.model';
import { SIDEBAR_SECTIONS } from './sidebar-sections.constants';

@Component({
  selector: 'jhi-sidebar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
  imports: [RouterLink, RouterLinkActive, FontAwesomeModule, TranslateDirective],
})
export default class Sidebar implements OnInit {
  readonly layoutState = inject(LayoutStateService);
  readonly sections = SIDEBAR_SECTIONS;
  readonly openApiEnabled = signal(false);

  readonly account = inject(AccountService).account;
  readonly permissions = inject(CurrentUserPermissionsService).permissions;

  /** Grand admins bypass per-item Permission checks entirely - see isItemVisible(). */
  readonly isGrandAdmin = computed(() => this.account()?.authorities?.includes(Authority.ADMIN) ?? false);

  private readonly profileService = inject(ProfileService);
  private readonly collapsedSections = signal<Set<string>>(new Set());

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.openApiEnabled.set(profileInfo.openAPIEnabled ?? false);
    });
  }

  /** Whether a whole section should render, based on Spring authorities (e.g. Administration). */
  isSectionVisible(authorities?: string[]): boolean {
    if (!authorities || authorities.length === 0) {
      return true;
    }
    const userAuthorities = this.account()?.authorities ?? [];
    return authorities.some(authority => userAuthorities.includes(authority));
  }

  /** Whether a single item should render, based on the user's Permission flags. */
  isItemVisible(item: SidebarNavItem): boolean {
    if (!item.permissionKey) {
      return true;
    }
    if (this.isGrandAdmin()) {
      return true;
    }
    return this.permissions()[item.permissionKey];
  }

  isSectionCollapsed(sectionId: string): boolean {
    return this.collapsedSections().has(sectionId);
  }

  toggleSection(sectionId: string): void {
    this.collapsedSections.update(current => {
      const next = new Set(current);
      if (next.has(sectionId)) {
        next.delete(sectionId);
      } else {
        next.add(sectionId);
      }
      return next;
    });
  }
}
