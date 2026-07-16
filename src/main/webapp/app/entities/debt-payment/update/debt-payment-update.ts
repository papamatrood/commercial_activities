import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IDebt } from 'app/entities/debt/debt.model';
import { DebtService } from 'app/entities/debt/service/debt.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IDebtPayment } from '../debt-payment.model';
import { DebtPaymentService } from '../service/debt-payment.service';

import { DebtPaymentFormGroup, DebtPaymentFormService } from './debt-payment-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-debt-payment-update',
  templateUrl: './debt-payment-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class DebtPaymentUpdate implements OnInit {
  readonly isSaving = signal(false);
  debtPayment: IDebtPayment | null = null;

  debtsSharedCollection = signal<IDebt[]>([]);

  protected debtPaymentService = inject(DebtPaymentService);
  protected debtPaymentFormService = inject(DebtPaymentFormService);
  protected debtService = inject(DebtService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DebtPaymentFormGroup = this.debtPaymentFormService.createDebtPaymentFormGroup();

  compareDebt = (o1: IDebt | null, o2: IDebt | null): boolean => this.debtService.compareDebt(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ debtPayment }) => {
      this.debtPayment = debtPayment;
      if (debtPayment) {
        this.updateForm(debtPayment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const debtPayment = this.debtPaymentFormService.getDebtPayment(this.editForm);
    if (debtPayment.id === null) {
      this.subscribeToSaveResponse(this.debtPaymentService.create(debtPayment));
    } else {
      this.subscribeToSaveResponse(this.debtPaymentService.update(debtPayment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDebtPayment | null>): void {
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

  protected updateForm(debtPayment: IDebtPayment): void {
    this.debtPayment = debtPayment;
    this.debtPaymentFormService.resetForm(this.editForm, debtPayment);

    this.debtsSharedCollection.update(debts => this.debtService.addDebtToCollectionIfMissing<IDebt>(debts, debtPayment.debt));
  }

  protected loadRelationshipsOptions(): void {
    this.debtService
      .query()
      .pipe(map((res: HttpResponse<IDebt[]>) => res.body ?? []))
      .pipe(map((debts: IDebt[]) => this.debtService.addDebtToCollectionIfMissing<IDebt>(debts, this.debtPayment?.debt)))
      .subscribe((debts: IDebt[]) => this.debtsSharedCollection.set(debts));
  }
}
