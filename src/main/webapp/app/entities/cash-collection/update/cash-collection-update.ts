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
import { ICashCollection } from '../cash-collection.model';
import { CashCollectionService } from '../service/cash-collection.service';

import { CashCollectionFormGroup, CashCollectionFormService } from './cash-collection-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-cash-collection-update',
  templateUrl: './cash-collection-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CashCollectionUpdate implements OnInit {
  readonly isSaving = signal(false);
  cashCollection: ICashCollection | null = null;

  companiesSharedCollection = signal<ICompany[]>([]);
  appUsersSharedCollection = signal<IAppUser[]>([]);

  protected cashCollectionService = inject(CashCollectionService);
  protected cashCollectionFormService = inject(CashCollectionFormService);
  protected companyService = inject(CompanyService);
  protected appUserService = inject(AppUserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CashCollectionFormGroup = this.cashCollectionFormService.createCashCollectionFormGroup();

  compareCompany = (o1: ICompany | null, o2: ICompany | null): boolean => this.companyService.compareCompany(o1, o2);

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cashCollection }) => {
      this.cashCollection = cashCollection;
      if (cashCollection) {
        this.updateForm(cashCollection);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const cashCollection = this.cashCollectionFormService.getCashCollection(this.editForm);
    if (cashCollection.id === null) {
      this.subscribeToSaveResponse(this.cashCollectionService.create(cashCollection));
    } else {
      this.subscribeToSaveResponse(this.cashCollectionService.update(cashCollection));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICashCollection | null>): void {
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

  protected updateForm(cashCollection: ICashCollection): void {
    this.cashCollection = cashCollection;
    this.cashCollectionFormService.resetForm(this.editForm, cashCollection);

    this.companiesSharedCollection.update(companies =>
      this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, cashCollection.company),
    );
    this.appUsersSharedCollection.update(appUsers =>
      this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, cashCollection.user),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.companyService
      .query()
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(
        map((companies: ICompany[]) =>
          this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, this.cashCollection?.company),
        ),
      )
      .subscribe((companies: ICompany[]) => this.companiesSharedCollection.set(companies));

    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) => this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.cashCollection?.user)),
      )
      .subscribe((appUsers: IAppUser[]) => this.appUsersSharedCollection.set(appUsers));
  }
}
