import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPermission, NewPermission } from '../permission.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPermission for edit and NewPermissionFormGroupInput for create.
 */
type PermissionFormGroupInput = IPermission | PartialWithRequiredKeyOf<NewPermission>;

type PermissionFormDefaults = Pick<NewPermission, 'id' | 'appUsers'>;

type PermissionFormGroupContent = {
  id: FormControl<IPermission['id'] | NewPermission['id']>;
  code: FormControl<IPermission['code']>;
  label: FormControl<IPermission['label']>;
  appUsers: FormControl<IPermission['appUsers']>;
};

export type PermissionFormGroup = FormGroup<PermissionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PermissionFormService {
  createPermissionFormGroup(permission?: PermissionFormGroupInput): PermissionFormGroup {
    const permissionRawValue = {
      ...this.getFormDefaults(),
      ...(permission ?? { id: null }),
    };

    return new FormGroup<PermissionFormGroupContent>({
      id: new FormControl(
        { value: permissionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(permissionRawValue.code, {
        validators: [Validators.required],
      }),
      label: new FormControl(permissionRawValue.label, {
        validators: [Validators.required],
      }),
      appUsers: new FormControl(permissionRawValue.appUsers ?? []),
    });
  }

  getPermission(form: PermissionFormGroup): IPermission | NewPermission {
    return form.getRawValue();
  }

  resetForm(form: PermissionFormGroup, permission: PermissionFormGroupInput): void {
    const permissionRawValue = { ...this.getFormDefaults(), ...permission };
    form.reset({
      ...permissionRawValue,
      id: { value: permissionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PermissionFormDefaults {
    return {
      id: null,
      appUsers: [],
    };
  }
}
