import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICashCollection, NewCashCollection } from '../cash-collection.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICashCollection for edit and NewCashCollectionFormGroupInput for create.
 */
type CashCollectionFormGroupInput = ICashCollection | PartialWithRequiredKeyOf<NewCashCollection>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICashCollection | NewCashCollection> = Omit<T, 'date'> & {
  date?: string | null;
};

type CashCollectionFormRawValue = FormValueOf<ICashCollection>;

type NewCashCollectionFormRawValue = FormValueOf<NewCashCollection>;

type CashCollectionFormDefaults = Pick<NewCashCollection, 'id' | 'date'>;

type CashCollectionFormGroupContent = {
  id: FormControl<CashCollectionFormRawValue['id'] | NewCashCollection['id']>;
  date: FormControl<CashCollectionFormRawValue['date']>;
  amount: FormControl<CashCollectionFormRawValue['amount']>;
  company: FormControl<CashCollectionFormRawValue['company']>;
  user: FormControl<CashCollectionFormRawValue['user']>;
};

export type CashCollectionFormGroup = FormGroup<CashCollectionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CashCollectionFormService {
  createCashCollectionFormGroup(cashCollection?: CashCollectionFormGroupInput): CashCollectionFormGroup {
    const cashCollectionRawValue = this.convertCashCollectionToCashCollectionRawValue({
      ...this.getFormDefaults(),
      ...(cashCollection ?? { id: null }),
    });

    return new FormGroup<CashCollectionFormGroupContent>({
      id: new FormControl(
        { value: cashCollectionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(cashCollectionRawValue.date, {
        validators: [Validators.required],
      }),
      amount: new FormControl(cashCollectionRawValue.amount, {
        validators: [Validators.required],
      }),
      company: new FormControl(cashCollectionRawValue.company),
      user: new FormControl(cashCollectionRawValue.user),
    });
  }

  getCashCollection(form: CashCollectionFormGroup): ICashCollection | NewCashCollection {
    return this.convertCashCollectionRawValueToCashCollection(form.getRawValue());
  }

  resetForm(form: CashCollectionFormGroup, cashCollection: CashCollectionFormGroupInput): void {
    const cashCollectionRawValue = this.convertCashCollectionToCashCollectionRawValue({ ...this.getFormDefaults(), ...cashCollection });
    form.reset({
      ...cashCollectionRawValue,
      id: { value: cashCollectionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CashCollectionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertCashCollectionRawValueToCashCollection(
    rawCashCollection: CashCollectionFormRawValue | NewCashCollectionFormRawValue,
  ): ICashCollection | NewCashCollection {
    return {
      ...rawCashCollection,
      date: dayjs(rawCashCollection.date, DATE_TIME_FORMAT),
    };
  }

  private convertCashCollectionToCashCollectionRawValue(
    cashCollection: ICashCollection | (Partial<NewCashCollection> & CashCollectionFormDefaults),
  ): CashCollectionFormRawValue | PartialWithRequiredKeyOf<NewCashCollectionFormRawValue> {
    return {
      ...cashCollection,
      date: cashCollection.date ? cashCollection.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
