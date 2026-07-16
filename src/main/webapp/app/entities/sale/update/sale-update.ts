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
import { ISale } from '../sale.model';
import { SaleService } from '../service/sale.service';

import { SaleFormGroup, SaleFormService } from './sale-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-sale-update',
  templateUrl: './sale-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SaleUpdate implements OnInit {
  readonly isSaving = signal(false);
  sale: ISale | null = null;

  companiesSharedCollection = signal<ICompany[]>([]);
  appUsersSharedCollection = signal<IAppUser[]>([]);

  protected saleService = inject(SaleService);
  protected saleFormService = inject(SaleFormService);
  protected companyService = inject(CompanyService);
  protected appUserService = inject(AppUserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SaleFormGroup = this.saleFormService.createSaleFormGroup();

  compareCompany = (o1: ICompany | null, o2: ICompany | null): boolean => this.companyService.compareCompany(o1, o2);

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sale }) => {
      this.sale = sale;
      if (sale) {
        this.updateForm(sale);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const sale = this.saleFormService.getSale(this.editForm);
    if (sale.id === null) {
      this.subscribeToSaveResponse(this.saleService.create(sale));
    } else {
      this.subscribeToSaveResponse(this.saleService.update(sale));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISale | null>): void {
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

  protected updateForm(sale: ISale): void {
    this.sale = sale;
    this.saleFormService.resetForm(this.editForm, sale);

    this.companiesSharedCollection.update(companies =>
      this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, sale.company),
    );
    this.appUsersSharedCollection.update(appUsers => this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, sale.seller));
  }

  protected loadRelationshipsOptions(): void {
    this.companyService
      .query()
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(map((companies: ICompany[]) => this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, this.sale?.company)))
      .subscribe((companies: ICompany[]) => this.companiesSharedCollection.set(companies));

    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(map((appUsers: IAppUser[]) => this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.sale?.seller)))
      .subscribe((appUsers: IAppUser[]) => this.appUsersSharedCollection.set(appUsers));
  }
}
