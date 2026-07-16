import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDebtPayment, NewDebtPayment } from '../debt-payment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDebtPayment for edit and NewDebtPaymentFormGroupInput for create.
 */
type DebtPaymentFormGroupInput = IDebtPayment | PartialWithRequiredKeyOf<NewDebtPayment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDebtPayment | NewDebtPayment> = Omit<T, 'date'> & {
  date?: string | null;
};

type DebtPaymentFormRawValue = FormValueOf<IDebtPayment>;

type NewDebtPaymentFormRawValue = FormValueOf<NewDebtPayment>;

type DebtPaymentFormDefaults = Pick<NewDebtPayment, 'id' | 'date'>;

type DebtPaymentFormGroupContent = {
  id: FormControl<DebtPaymentFormRawValue['id'] | NewDebtPayment['id']>;
  amountPaid: FormControl<DebtPaymentFormRawValue['amountPaid']>;
  remainingAmount: FormControl<DebtPaymentFormRawValue['remainingAmount']>;
  date: FormControl<DebtPaymentFormRawValue['date']>;
  debt: FormControl<DebtPaymentFormRawValue['debt']>;
};

export type DebtPaymentFormGroup = FormGroup<DebtPaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DebtPaymentFormService {
  createDebtPaymentFormGroup(debtPayment?: DebtPaymentFormGroupInput): DebtPaymentFormGroup {
    const debtPaymentRawValue = this.convertDebtPaymentToDebtPaymentRawValue({
      ...this.getFormDefaults(),
      ...(debtPayment ?? { id: null }),
    });

    return new FormGroup<DebtPaymentFormGroupContent>({
      id: new FormControl(
        { value: debtPaymentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      amountPaid: new FormControl(debtPaymentRawValue.amountPaid, {
        validators: [Validators.required],
      }),
      remainingAmount: new FormControl(debtPaymentRawValue.remainingAmount),
      date: new FormControl(debtPaymentRawValue.date, {
        validators: [Validators.required],
      }),
      debt: new FormControl(debtPaymentRawValue.debt),
    });
  }

  getDebtPayment(form: DebtPaymentFormGroup): IDebtPayment | NewDebtPayment {
    return this.convertDebtPaymentRawValueToDebtPayment(form.getRawValue());
  }

  resetForm(form: DebtPaymentFormGroup, debtPayment: DebtPaymentFormGroupInput): void {
    const debtPaymentRawValue = this.convertDebtPaymentToDebtPaymentRawValue({ ...this.getFormDefaults(), ...debtPayment });
    form.reset({
      ...debtPaymentRawValue,
      id: { value: debtPaymentRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DebtPaymentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertDebtPaymentRawValueToDebtPayment(
    rawDebtPayment: DebtPaymentFormRawValue | NewDebtPaymentFormRawValue,
  ): IDebtPayment | NewDebtPayment {
    return {
      ...rawDebtPayment,
      date: dayjs(rawDebtPayment.date, DATE_TIME_FORMAT),
    };
  }

  private convertDebtPaymentToDebtPaymentRawValue(
    debtPayment: IDebtPayment | (Partial<NewDebtPayment> & DebtPaymentFormDefaults),
  ): DebtPaymentFormRawValue | PartialWithRequiredKeyOf<NewDebtPaymentFormRawValue> {
    return {
      ...debtPayment,
      date: debtPayment.date ? debtPayment.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
