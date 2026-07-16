import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';
import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICashDisbursement } from '../cash-disbursement.model';
import { CashDisbursementService } from '../service/cash-disbursement.service';

import { CashDisbursementFormGroup, CashDisbursementFormService } from './cash-disbursement-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-cash-disbursement-update',
  templateUrl: './cash-disbursement-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CashDisbursementUpdate implements OnInit {
  readonly isSaving = signal(false);
  cashDisbursement: ICashDisbursement | null = null;

  companiesSharedCollection = signal<ICompany[]>([]);
  appUsersSharedCollection = signal<IAppUser[]>([]);

  protected cashDisbursementService = inject(CashDisbursementService);
  protected cashDisbursementFormService = inject(CashDisbursementFormService);
  protected companyService = inject(CompanyService);
  protected appUserService = inject(AppUserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CashDisbursementFormGroup = this.cashDisbursementFormService.createCashDisbursementFormGroup();

  compareCompany = (o1: ICompany | null, o2: ICompany | null): boolean => this.companyService.compareCompany(o1, o2);

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cashDisbursement }) => {
      this.cashDisbursement = cashDisbursement;
      if (cashDisbursement) {
        this.updateForm(cashDisbursement);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const cashDisbursement = this.cashDisbursementFormService.getCashDisbursement(this.editForm);
    if (cashDisbursement.id === null) {
      this.subscribeToSaveResponse(this.cashDisbursementService.create(cashDisbursement));
    } else {
      this.subscribeToSaveResponse(this.cashDisbursementService.update(cashDisbursement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICashDisbursement | null>): void {
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

  protected updateForm(cashDisbursement: ICashDisbursement): void {
    this.cashDisbursement = cashDisbursement;
    this.cashDisbursementFormService.resetForm(this.editForm, cashDisbursement);

    this.companiesSharedCollection.update(companies =>
      this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, cashDisbursement.company),
    );
    this.appUsersSharedCollection.update(appUsers =>
      this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, cashDisbursement.user),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.companyService
      .query()
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(
        map((companies: ICompany[]) =>
          this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, this.cashDisbursement?.company),
        ),
      )
      .subscribe((companies: ICompany[]) => this.companiesSharedCollection.set(companies));

    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) => this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.cashDisbursement?.user)),
      )
      .subscribe((appUsers: IAppUser[]) => this.appUsersSharedCollection.set(appUsers));
  }
}
