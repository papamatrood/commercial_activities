import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPermission } from '../permission.model';
import { PermissionService } from '../service/permission.service';

import { PermissionFormGroup, PermissionFormService } from './permission-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-permission-update',
  templateUrl: './permission-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PermissionUpdate implements OnInit {
  readonly isSaving = signal(false);
  permission: IPermission | null = null;

  appUsersSharedCollection = signal<IAppUser[]>([]);

  protected permissionService = inject(PermissionService);
  protected permissionFormService = inject(PermissionFormService);
  protected appUserService = inject(AppUserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PermissionFormGroup = this.permissionFormService.createPermissionFormGroup();

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ permission }) => {
      this.permission = permission;
      if (permission) {
        this.updateForm(permission);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const permission = this.permissionFormService.getPermission(this.editForm);
    if (permission.id === null) {
      this.subscribeToSaveResponse(this.permissionService.create(permission));
    } else {
      this.subscribeToSaveResponse(this.permissionService.update(permission));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPermission | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(permission: IPermission): void {
    this.permission = permission;
    this.permissionFormService.resetForm(this.editForm, permission);

    this.appUsersSharedCollection.update(appUsers =>
      this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, ...(permission.appUsers ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) =>
          this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, ...(this.permission?.appUsers ?? [])),
        ),
      )
      .subscribe((appUsers: IAppUser[]) => this.appUsersSharedCollection.set(appUsers));
  }
}
