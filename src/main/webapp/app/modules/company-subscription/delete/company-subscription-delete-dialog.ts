import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICompanySubscription } from 'app/entities/company-subscription/company-subscription.model';
import { CompanySubscriptionService } from 'app/entities/company-subscription/service/company-subscription.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './company-subscription-delete-dialog.html',
  imports: [TranslateDirective, FormsModule, FontAwesomeModule, AlertError],
})
export class CompanySubscriptionDeleteDialog {
  companySubscription?: ICompanySubscription;

  protected readonly companySubscriptionService = inject(CompanySubscriptionService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.companySubscriptionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
