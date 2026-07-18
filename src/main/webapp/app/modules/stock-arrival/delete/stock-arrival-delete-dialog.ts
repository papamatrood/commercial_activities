import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { StockArrivalService } from 'app/entities/stock-arrival/service/stock-arrival.service';
import { IStockArrival } from 'app/entities/stock-arrival/stock-arrival.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './stock-arrival-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class StockArrivalDeleteDialog {
  stockArrival?: IStockArrival;

  protected readonly stockArrivalService = inject(StockArrivalService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.stockArrivalService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
