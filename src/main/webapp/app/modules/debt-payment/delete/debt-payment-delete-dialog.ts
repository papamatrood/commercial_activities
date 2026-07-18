import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IDebtPayment } from 'app/entities/debt-payment/debt-payment.model';
import { DebtPaymentService } from 'app/entities/debt-payment/service/debt-payment.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './debt-payment-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class DebtPaymentDeleteDialog {
  debtPayment?: IDebtPayment;

  protected readonly debtPaymentService = inject(DebtPaymentService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.debtPaymentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
