import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDebt, NewDebt } from '../debt.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDebt for edit and NewDebtFormGroupInput for create.
 */
type DebtFormGroupInput = IDebt | PartialWithRequiredKeyOf<NewDebt>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDebt | NewDebt> = Omit<T, 'date'> & {
  date?: string | null;
};

type DebtFormRawValue = FormValueOf<IDebt>;

type NewDebtFormRawValue = FormValueOf<NewDebt>;

type DebtFormDefaults = Pick<NewDebt, 'id' | 'date'>;

type DebtFormGroupContent = {
  id: FormControl<DebtFormRawValue['id'] | NewDebt['id']>;
  totalAmount: FormControl<DebtFormRawValue['totalAmount']>;
  amountPaid: FormControl<DebtFormRawValue['amountPaid']>;
  remainingAmount: FormControl<DebtFormRawValue['remainingAmount']>;
  date: FormControl<DebtFormRawValue['date']>;
  company: FormControl<DebtFormRawValue['company']>;
  sale: FormControl<DebtFormRawValue['sale']>;
};

export type DebtFormGroup = FormGroup<DebtFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DebtFormService {
  createDebtFormGroup(debt?: DebtFormGroupInput): DebtFormGroup {
    const debtRawValue = this.convertDebtToDebtRawValue({
      ...this.getFormDefaults(),
      ...(debt ?? { id: null }),
    });

    return new FormGroup<DebtFormGroupContent>({
      id: new FormControl(
        { value: debtRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      totalAmount: new FormControl(debtRawValue.totalAmount, {
        validators: [Validators.required],
      }),
      amountPaid: new FormControl(debtRawValue.amountPaid),
      remainingAmount: new FormControl(debtRawValue.remainingAmount),
      date: new FormControl(debtRawValue.date, {
        validators: [Validators.required],
      }),
      company: new FormControl(debtRawValue.company),
      sale: new FormControl(debtRawValue.sale),
    });
  }

  getDebt(form: DebtFormGroup): IDebt | NewDebt {
    return this.convertDebtRawValueToDebt(form.getRawValue());
  }

  resetForm(form: DebtFormGroup, debt: DebtFormGroupInput): void {
    const debtRawValue = this.convertDebtToDebtRawValue({ ...this.getFormDefaults(), ...debt });
    form.reset({
      ...debtRawValue,
      id: { value: debtRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DebtFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertDebtRawValueToDebt(rawDebt: DebtFormRawValue | NewDebtFormRawValue): IDebt | NewDebt {
    return {
      ...rawDebt,
      date: dayjs(rawDebt.date, DATE_TIME_FORMAT),
    };
  }

  private convertDebtToDebtRawValue(
    debt: IDebt | (Partial<NewDebt> & DebtFormDefaults),
  ): DebtFormRawValue | PartialWithRequiredKeyOf<NewDebtFormRawValue> {
    return {
      ...debt,
      date: debt.date ? debt.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
