import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { LANGUAGES } from 'app/config/language.constants';
import { AppUserTypeEnum } from 'app/entities/enumerations/app-user-type-enum.model';
import { GenderEnum } from 'app/entities/enumerations/gender-enum.model';
import { CompanyStatusEnum } from 'app/entities/enumerations/company-status-enum.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { FindLanguageFromKeyPipe, TranslateDirective } from 'app/shared/language';

import { ICompanyWithOwner, NewCompanyWithOwner } from '../company-owner.model';
import { CompanyOwnerService } from '../company-owner.service';
import { CompanyFormGroup, CompanyFormService } from './company-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-company-update',
  templateUrl: './company-update.html',
  styleUrl: './company-update.scss',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule, FindLanguageFromKeyPipe],
})
export class CompanyUpdate implements OnInit {
  readonly isSaving = signal(false);
  companyWithOwner: ICompanyWithOwner | null = null;
  readonly companyStatusEnumValues = Object.keys(CompanyStatusEnum);
  readonly appUserTypeEnumValues = Object.keys(AppUserTypeEnum);
  readonly genderEnumValues = Object.keys(GenderEnum);
  readonly languages = LANGUAGES;

  protected companyOwnerService = inject(CompanyOwnerService);
  protected companyFormService = inject(CompanyFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CompanyFormGroup = this.companyFormService.createCompanyFormGroup();

  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly passwordHint = computed(() => {
    const login = this.editForm.controls.owner.controls.login.value;
    return login ? `${login.toLowerCase()}123` : '';
  });

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ companyWithOwner }) => {
      this.companyWithOwner = companyWithOwner;
      if (companyWithOwner) {
        this.updateForm(companyWithOwner);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const companyId = this.editForm.controls.company.controls.id.value;
    const companyWithOwner = this.companyFormService.getCompanyWithOwner(this.editForm);
    if (companyId === null) {
      this.subscribeToSaveResponse(this.companyOwnerService.create(companyWithOwner as NewCompanyWithOwner));
    } else {
      this.subscribeToSaveResponse(this.companyOwnerService.update(companyId, companyWithOwner as ICompanyWithOwner));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICompanyWithOwner | null>): void {
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

  protected updateForm(companyWithOwner: ICompanyWithOwner): void {
    this.companyWithOwner = companyWithOwner;
    this.companyFormService.resetForm(this.editForm, companyWithOwner);
  }
}
