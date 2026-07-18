import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICashDisbursement } from 'app/entities/cash-disbursement/cash-disbursement.model';
import { CashDisbursementService } from 'app/entities/cash-disbursement/service/cash-disbursement.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './cash-disbursement-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class CashDisbursementDeleteDialog {
  cashDisbursement?: ICashDisbursement;

  protected readonly cashDisbursementService = inject(CashDisbursementService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cashDisbursementService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
