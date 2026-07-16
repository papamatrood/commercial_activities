import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IDebt } from '../debt.model';
import { DebtService } from '../service/debt.service';

import { DebtFormGroup, DebtFormService } from './debt-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-debt-update',
  templateUrl: './debt-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class DebtUpdate implements OnInit {
  readonly isSaving = signal(false);
  debt: IDebt | null = null;

  companiesSharedCollection = signal<ICompany[]>([]);
  salesSharedCollection = signal<ISale[]>([]);

  protected debtService = inject(DebtService);
  protected debtFormService = inject(DebtFormService);
  protected companyService = inject(CompanyService);
  protected saleService = inject(SaleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DebtFormGroup = this.debtFormService.createDebtFormGroup();

  compareCompany = (o1: ICompany | null, o2: ICompany | null): boolean => this.companyService.compareCompany(o1, o2);

  compareSale = (o1: ISale | null, o2: ISale | null): boolean => this.saleService.compareSale(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ debt }) => {
      this.debt = debt;
      if (debt) {
        this.updateForm(debt);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const debt = this.debtFormService.getDebt(this.editForm);
    if (debt.id === null) {
      this.subscribeToSaveResponse(this.debtService.create(debt));
    } else {
      this.subscribeToSaveResponse(this.debtService.update(debt));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDebt | null>): void {
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

  protected updateForm(debt: IDebt): void {
    this.debt = debt;
    this.debtFormService.resetForm(this.editForm, debt);

    this.companiesSharedCollection.update(companies =>
      this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, debt.company),
    );
    this.salesSharedCollection.update(sales => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, debt.sale));
  }

  protected loadRelationshipsOptions(): void {
    this.companyService
      .query()
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(map((companies: ICompany[]) => this.companyService.addCompanyToCollectionIfMissing<ICompany>(companies, this.debt?.company)))
      .subscribe((companies: ICompany[]) => this.companiesSharedCollection.set(companies));

    this.saleService
      .query()
      .pipe(map((res: HttpResponse<ISale[]>) => res.body ?? []))
      .pipe(map((sales: ISale[]) => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, this.debt?.sale)))
      .subscribe((sales: ISale[]) => this.salesSharedCollection.set(sales));
  }
}
