import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISale, NewSale } from '../sale.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISale for edit and NewSaleFormGroupInput for create.
 */
type SaleFormGroupInput = ISale | PartialWithRequiredKeyOf<NewSale>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISale | NewSale> = Omit<T, 'date'> & {
  date?: string | null;
};

type SaleFormRawValue = FormValueOf<ISale>;

type NewSaleFormRawValue = FormValueOf<NewSale>;

type SaleFormDefaults = Pick<NewSale, 'id' | 'date'>;

type SaleFormGroupContent = {
  id: FormControl<SaleFormRawValue['id'] | NewSale['id']>;
  date: FormControl<SaleFormRawValue['date']>;
  amountToPay: FormControl<SaleFormRawValue['amountToPay']>;
  amountPaid: FormControl<SaleFormRawValue['amountPaid']>;
  remainingAmount: FormControl<SaleFormRawValue['remainingAmount']>;
  customerName: FormControl<SaleFormRawValue['customerName']>;
  customerCompany: FormControl<SaleFormRawValue['customerCompany']>;
  customerContact: FormControl<SaleFormRawValue['customerContact']>;
  company: FormControl<SaleFormRawValue['company']>;
  seller: FormControl<SaleFormRawValue['seller']>;
};

export type SaleFormGroup = FormGroup<SaleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SaleFormService {
  createSaleFormGroup(sale?: SaleFormGroupInput): SaleFormGroup {
    const saleRawValue = this.convertSaleToSaleRawValue({
      ...this.getFormDefaults(),
      ...(sale ?? { id: null }),
    });

    return new FormGroup<SaleFormGroupContent>({
      id: new FormControl(
        { value: saleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(saleRawValue.date, {
        validators: [Validators.required],
      }),
      amountToPay: new FormControl(saleRawValue.amountToPay, {
        validators: [Validators.required],
      }),
      amountPaid: new FormControl(saleRawValue.amountPaid, {
        validators: [Validators.required],
      }),
      remainingAmount: new FormControl(saleRawValue.remainingAmount),
      customerName: new FormControl(saleRawValue.customerName),
      customerCompany: new FormControl(saleRawValue.customerCompany),
      customerContact: new FormControl(saleRawValue.customerContact),
      company: new FormControl(saleRawValue.company),
      seller: new FormControl(saleRawValue.seller),
    });
  }

  getSale(form: SaleFormGroup): ISale | NewSale {
    return this.convertSaleRawValueToSale(form.getRawValue());
  }

  resetForm(form: SaleFormGroup, sale: SaleFormGroupInput): void {
    const saleRawValue = this.convertSaleToSaleRawValue({ ...this.getFormDefaults(), ...sale });
    form.reset({
      ...saleRawValue,
      id: { value: saleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SaleFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertSaleRawValueToSale(rawSale: SaleFormRawValue | NewSaleFormRawValue): ISale | NewSale {
    return {
      ...rawSale,
      date: dayjs(rawSale.date, DATE_TIME_FORMAT),
    };
  }

  private convertSaleToSaleRawValue(
    sale: ISale | (Partial<NewSale> & SaleFormDefaults),
  ): SaleFormRawValue | PartialWithRequiredKeyOf<NewSaleFormRawValue> {
    return {
      ...sale,
      date: sale.date ? sale.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
