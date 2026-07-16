import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { AppUserTypeEnum } from 'app/entities/enumerations/app-user-type-enum.model';
import { GenderEnum } from 'app/entities/enumerations/gender-enum.model';
import { IPermission } from 'app/entities/permission/permission.model';
import { PermissionService } from 'app/entities/permission/service/permission.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IAppUser } from '../app-user.model';
import { AppUserService } from '../service/app-user.service';

import { AppUserFormGroup, AppUserFormService } from './app-user-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-app-user-update',
  templateUrl: './app-user-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class AppUserUpdate implements OnInit {
  readonly isSaving = signal(false);
  appUser: IAppUser | null = null;
  appUserTypeEnumValues = Object.keys(AppUserTypeEnum);
  genderEnumValues = Object.keys(GenderEnum);

  usersSharedCollection = signal<IUser[]>([]);
  companiesSharedCollection = signal<ICompany[]>([]);
  permissionsSharedCollection = signal<IPermission[]>([]);

  protected appUserService = inject(AppUserService);
  protected appUserFormService = inject(AppUserFormService);
  protected userService = inject(UserService);
  protected companyService = inject(CompanyService);
  protected permissionService = inject(PermissionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AppUserFormGroup = this.appUserFormService.createAppUserFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCompany = (o1: ICompany | null, o2: ICompany | null): boolean => this.companyService.compareCompany(o1, o2);

  comparePermission = (o1: IPermission | null, o2: IPermission | null): boolean => this.permissionService.comparePermission(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appUser }) => {
      this.appUser = appUser;
      if (appUser) {
        this.updateForm(appUser);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const appUser = this.appUserFormService.getAppUser(this.editForm);
    if (appUser.id === null) {
      this.subscribeToSaveResponse(this.appUserService.create(appUser));
    } else {
      this.subscribeToSaveResponse(this.appUserService.update(appUser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAppUser | null>): void {
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

  protected updateForm(appUser: IAppUser): void {
    this.appUser = appUser;
    this.appUserFormService.resetForm(this.editForm, appUser);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, appUser.user));
    this.companiesSharedCollection.update(companies =>
      this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, appUser.company),
    );
    this.permissionsSharedCollection.update(permissions =>
      this.permissionService.addPermissionToCollectionIfMissing<IPermission>(permissions, ...(appUser.permissions ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.appUser?.user)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.companyService
      .query()
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(map((companies: ICompany[]) => this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, this.appUser?.company)))
      .subscribe((companies: ICompany[]) => this.companiesSharedCollection.set(companies));

    this.permissionService
      .query()
      .pipe(map((res: HttpResponse<IPermission[]>) => res.body ?? []))
      .pipe(
        map((permissions: IPermission[]) =>
          this.permissionService.addPermissionToCollectionIfMissing<IPermission>(permissions, ...(this.appUser?.permissions ?? [])),
        ),
      )
      .subscribe((permissions: IPermission[]) => this.permissionsSharedCollection.set(permissions));
  }
}
