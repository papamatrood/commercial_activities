import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { CompanySubscriptionStatusEnum } from 'app/entities/enumerations/company-subscription-status-enum.model';
import { CompanySubscriptionTypeEnum } from 'app/entities/enumerations/company-subscription-type-enum.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICompanySubscription } from '../company-subscription.model';
import { CompanySubscriptionService } from '../service/company-subscription.service';

import { CompanySubscriptionFormGroup, CompanySubscriptionFormService } from './company-subscription-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-company-subscription-update',
  templateUrl: './company-subscription-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CompanySubscriptionUpdate implements OnInit {
  readonly isSaving = signal(false);
  companySubscription: ICompanySubscription | null = null;
  companySubscriptionTypeEnumValues = Object.keys(CompanySubscriptionTypeEnum);
  companySubscriptionStatusEnumValues = Object.keys(CompanySubscriptionStatusEnum);

  companiesSharedCollection = signal<ICompany[]>([]);

  protected companySubscriptionService = inject(CompanySubscriptionService);
  protected companySubscriptionFormService = inject(CompanySubscriptionFormService);
  protected companyService = inject(CompanyService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CompanySubscriptionFormGroup = this.companySubscriptionFormService.createCompanySubscriptionFormGroup();

  compareCompany = (o1: ICompany | null, o2: ICompany | null): boolean => this.companyService.compareCompany(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ companySubscription }) => {
      this.companySubscription = companySubscription;
      if (companySubscription) {
        this.updateForm(companySubscription);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const companySubscription = this.companySubscriptionFormService.getCompanySubscription(this.editForm);
    if (companySubscription.id === null) {
      this.subscribeToSaveResponse(this.companySubscriptionService.create(companySubscription));
    } else {
      this.subscribeToSaveResponse(this.companySubscriptionService.update(companySubscription));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICompanySubscription | null>): void {
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

  protected updateForm(companySubscription: ICompanySubscription): void {
    this.companySubscription = companySubscription;
    this.companySubscriptionFormService.resetForm(this.editForm, companySubscription);

    this.companiesSharedCollection.update(companies =>
      this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, companySubscription.company),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.companyService
      .query()
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(
        map((companies: ICompany[]) =>
          this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, this.companySubscription?.company),
        ),
      )
      .subscribe((companies: ICompany[]) => this.companiesSharedCollection.set(companies));
  }
}
