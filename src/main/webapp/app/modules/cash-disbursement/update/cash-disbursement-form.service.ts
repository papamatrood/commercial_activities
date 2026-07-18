import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICashDisbursement, NewCashDisbursement } from 'app/entities/cash-disbursement/cash-disbursement.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICashDisbursement for edit and NewCashDisbursementFormGroupInput for create.
 */
type CashDisbursementFormGroupInput = ICashDisbursement | PartialWithRequiredKeyOf<NewCashDisbursement>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICashDisbursement | NewCashDisbursement> = Omit<T, 'date'> & {
  date?: string | null;
};

type CashDisbursementFormRawValue = FormValueOf<ICashDisbursement>;

type NewCashDisbursementFormRawValue = FormValueOf<NewCashDisbursement>;

type CashDisbursementFormDefaults = Pick<NewCashDisbursement, 'id' | 'date'>;

type CashDisbursementFormGroupContent = {
  id: FormControl<CashDisbursementFormRawValue['id'] | NewCashDisbursement['id']>;
  reason: FormControl<CashDisbursementFormRawValue['reason']>;
  amount: FormControl<CashDisbursementFormRawValue['amount']>;
  date: FormControl<CashDisbursementFormRawValue['date']>;
  company: FormControl<CashDisbursementFormRawValue['company']>;
  user: FormControl<CashDisbursementFormRawValue['user']>;
};

export type CashDisbursementFormGroup = FormGroup<CashDisbursementFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CashDisbursementFormService {
  createCashDisbursementFormGroup(cashDisbursement?: CashDisbursementFormGroupInput): CashDisbursementFormGroup {
    const cashDisbursementRawValue = this.convertCashDisbursementToCashDisbursementRawValue({
      ...this.getFormDefaults(),
      ...(cashDisbursement ?? { id: null }),
    });

    return new FormGroup<CashDisbursementFormGroupContent>({
      id: new FormControl(
        { value: cashDisbursementRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reason: new FormControl(cashDisbursementRawValue.reason, {
        validators: [Validators.required],
      }),
      amount: new FormControl(cashDisbursementRawValue.amount, {
        validators: [Validators.required],
      }),
      date: new FormControl(cashDisbursementRawValue.date, {
        validators: [Validators.required],
      }),
      company: new FormControl(cashDisbursementRawValue.company),
      user: new FormControl(cashDisbursementRawValue.user),
    });
  }

  getCashDisbursement(form: CashDisbursementFormGroup): ICashDisbursement | NewCashDisbursement {
    return this.convertCashDisbursementRawValueToCashDisbursement(form.getRawValue());
  }

  resetForm(form: CashDisbursementFormGroup, cashDisbursement: CashDisbursementFormGroupInput): void {
    const cashDisbursementRawValue = this.convertCashDisbursementToCashDisbursementRawValue({
      ...this.getFormDefaults(),
      ...cashDisbursement,
    });
    form.reset({
      ...cashDisbursementRawValue,
      id: { value: cashDisbursementRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CashDisbursementFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertCashDisbursementRawValueToCashDisbursement(
    rawCashDisbursement: CashDisbursementFormRawValue | NewCashDisbursementFormRawValue,
  ): ICashDisbursement | NewCashDisbursement {
    return {
      ...rawCashDisbursement,
      date: dayjs(rawCashDisbursement.date, DATE_TIME_FORMAT),
    };
  }

  private convertCashDisbursementToCashDisbursementRawValue(
    cashDisbursement: ICashDisbursement | (Partial<NewCashDisbursement> & CashDisbursementFormDefaults),
  ): CashDisbursementFormRawValue | PartialWithRequiredKeyOf<NewCashDisbursementFormRawValue> {
    return {
      ...cashDisbursement,
      date: cashDisbursement.date ? cashDisbursement.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
