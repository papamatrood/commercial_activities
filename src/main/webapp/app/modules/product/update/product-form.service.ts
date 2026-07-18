import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProduct, NewProduct } from 'app/entities/product/product.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProduct for edit and NewProductFormGroupInput for create.
 */
type ProductFormGroupInput = IProduct | PartialWithRequiredKeyOf<NewProduct>;

type ProductFormDefaults = Pick<NewProduct, 'id'>;

type ProductFormGroupContent = {
  id: FormControl<IProduct['id'] | NewProduct['id']>;
  barcode: FormControl<IProduct['barcode']>;
  designation: FormControl<IProduct['designation']>;
  initialStock: FormControl<IProduct['initialStock']>;
  currentStock: FormControl<IProduct['currentStock']>;
  unitPrice: FormControl<IProduct['unitPrice']>;
  company: FormControl<IProduct['company']>;
  supplier: FormControl<IProduct['supplier']>;
};

export type ProductFormGroup = FormGroup<ProductFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductFormService {
  createProductFormGroup(product?: ProductFormGroupInput): ProductFormGroup {
    const productRawValue = {
      ...this.getFormDefaults(),
      ...(product ?? { id: null }),
    };

    return new FormGroup<ProductFormGroupContent>({
      id: new FormControl(
        { value: productRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      barcode: new FormControl(productRawValue.barcode, {
        validators: [Validators.required],
      }),
      designation: new FormControl(productRawValue.designation, {
        validators: [Validators.required],
      }),
      initialStock: new FormControl(productRawValue.initialStock),
      currentStock: new FormControl(productRawValue.currentStock),
      unitPrice: new FormControl(productRawValue.unitPrice, {
        validators: [Validators.required],
      }),
      company: new FormControl(productRawValue.company),
      supplier: new FormControl(productRawValue.supplier),
    });
  }

  getProduct(form: ProductFormGroup): IProduct | NewProduct {
    return form.getRawValue();
  }

  resetForm(form: ProductFormGroup, product: ProductFormGroupInput): void {
    const productRawValue = { ...this.getFormDefaults(), ...product };
    form.reset({
      ...productRawValue,
      id: { value: productRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProductFormDefaults {
    return {
      id: null,
    };
  }
}
