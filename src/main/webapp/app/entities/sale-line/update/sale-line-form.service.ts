import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISaleLine, NewSaleLine } from '../sale-line.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISaleLine for edit and NewSaleLineFormGroupInput for create.
 */
type SaleLineFormGroupInput = ISaleLine | PartialWithRequiredKeyOf<NewSaleLine>;

type SaleLineFormDefaults = Pick<NewSaleLine, 'id'>;

type SaleLineFormGroupContent = {
  id: FormControl<ISaleLine['id'] | NewSaleLine['id']>;
  barcode: FormControl<ISaleLine['barcode']>;
  quantity: FormControl<ISaleLine['quantity']>;
  unitPrice: FormControl<ISaleLine['unitPrice']>;
  totalPrice: FormControl<ISaleLine['totalPrice']>;
  sale: FormControl<ISaleLine['sale']>;
  product: FormControl<ISaleLine['product']>;
};

export type SaleLineFormGroup = FormGroup<SaleLineFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SaleLineFormService {
  createSaleLineFormGroup(saleLine?: SaleLineFormGroupInput): SaleLineFormGroup {
    const saleLineRawValue = {
      ...this.getFormDefaults(),
      ...(saleLine ?? { id: null }),
    };

    return new FormGroup<SaleLineFormGroupContent>({
      id: new FormControl(
        { value: saleLineRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      barcode: new FormControl(saleLineRawValue.barcode),
      quantity: new FormControl(saleLineRawValue.quantity, {
        validators: [Validators.required],
      }),
      unitPrice: new FormControl(saleLineRawValue.unitPrice, {
        validators: [Validators.required],
      }),
      totalPrice: new FormControl(saleLineRawValue.totalPrice),
      sale: new FormControl(saleLineRawValue.sale),
      product: new FormControl(saleLineRawValue.product),
    });
  }

  getSaleLine(form: SaleLineFormGroup): ISaleLine | NewSaleLine {
    return form.getRawValue();
  }

  resetForm(form: SaleLineFormGroup, saleLine: SaleLineFormGroupInput): void {
    const saleLineRawValue = { ...this.getFormDefaults(), ...saleLine };
    form.reset({
      ...saleLineRawValue,
      id: { value: saleLineRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SaleLineFormDefaults {
    return {
      id: null,
    };
  }
}
