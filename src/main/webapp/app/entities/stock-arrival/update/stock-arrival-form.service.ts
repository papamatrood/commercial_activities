import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IStockArrival, NewStockArrival } from '../stock-arrival.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStockArrival for edit and NewStockArrivalFormGroupInput for create.
 */
type StockArrivalFormGroupInput = IStockArrival | PartialWithRequiredKeyOf<NewStockArrival>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IStockArrival | NewStockArrival> = Omit<T, 'date'> & {
  date?: string | null;
};

type StockArrivalFormRawValue = FormValueOf<IStockArrival>;

type NewStockArrivalFormRawValue = FormValueOf<NewStockArrival>;

type StockArrivalFormDefaults = Pick<NewStockArrival, 'id' | 'date'>;

type StockArrivalFormGroupContent = {
  id: FormControl<StockArrivalFormRawValue['id'] | NewStockArrival['id']>;
  barcode: FormControl<StockArrivalFormRawValue['barcode']>;
  quantity: FormControl<StockArrivalFormRawValue['quantity']>;
  amount: FormControl<StockArrivalFormRawValue['amount']>;
  date: FormControl<StockArrivalFormRawValue['date']>;
  product: FormControl<StockArrivalFormRawValue['product']>;
};

export type StockArrivalFormGroup = FormGroup<StockArrivalFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StockArrivalFormService {
  createStockArrivalFormGroup(stockArrival?: StockArrivalFormGroupInput): StockArrivalFormGroup {
    const stockArrivalRawValue = this.convertStockArrivalToStockArrivalRawValue({
      ...this.getFormDefaults(),
      ...(stockArrival ?? { id: null }),
    });

    return new FormGroup<StockArrivalFormGroupContent>({
      id: new FormControl(
        { value: stockArrivalRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      barcode: new FormControl(stockArrivalRawValue.barcode),
      quantity: new FormControl(stockArrivalRawValue.quantity, {
        validators: [Validators.required],
      }),
      amount: new FormControl(stockArrivalRawValue.amount),
      date: new FormControl(stockArrivalRawValue.date, {
        validators: [Validators.required],
      }),
      product: new FormControl(stockArrivalRawValue.product),
    });
  }

  getStockArrival(form: StockArrivalFormGroup): IStockArrival | NewStockArrival {
    return this.convertStockArrivalRawValueToStockArrival(form.getRawValue());
  }

  resetForm(form: StockArrivalFormGroup, stockArrival: StockArrivalFormGroupInput): void {
    const stockArrivalRawValue = this.convertStockArrivalToStockArrivalRawValue({ ...this.getFormDefaults(), ...stockArrival });
    form.reset({
      ...stockArrivalRawValue,
      id: { value: stockArrivalRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): StockArrivalFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertStockArrivalRawValueToStockArrival(
    rawStockArrival: StockArrivalFormRawValue | NewStockArrivalFormRawValue,
  ): IStockArrival | NewStockArrival {
    return {
      ...rawStockArrival,
      date: dayjs(rawStockArrival.date, DATE_TIME_FORMAT),
    };
  }

  private convertStockArrivalToStockArrivalRawValue(
    stockArrival: IStockArrival | (Partial<NewStockArrival> & StockArrivalFormDefaults),
  ): StockArrivalFormRawValue | PartialWithRequiredKeyOf<NewStockArrivalFormRawValue> {
    return {
      ...stockArrival,
      date: stockArrival.date ? stockArrival.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
