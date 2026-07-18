import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAppUser, NewAppUser } from 'app/entities/app-user/app-user.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAppUser for edit and NewAppUserFormGroupInput for create.
 */
type AppUserFormGroupInput = IAppUser | PartialWithRequiredKeyOf<NewAppUser>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAppUser | NewAppUser> = Omit<T, 'birthDate' | 'disabledDate'> & {
  birthDate?: string | null;
  disabledDate?: string | null;
};

type AppUserFormRawValue = FormValueOf<IAppUser>;

type NewAppUserFormRawValue = FormValueOf<NewAppUser>;

type AppUserFormDefaults = Pick<NewAppUser, 'id' | 'birthDate' | 'disabled' | 'disabledDate' | 'permissions'>;

type AppUserFormGroupContent = {
  id: FormControl<AppUserFormRawValue['id'] | NewAppUser['id']>;
  phoneNumber: FormControl<AppUserFormRawValue['phoneNumber']>;
  type: FormControl<AppUserFormRawValue['type']>;
  birthDate: FormControl<AppUserFormRawValue['birthDate']>;
  birthPlace: FormControl<AppUserFormRawValue['birthPlace']>;
  gender: FormControl<AppUserFormRawValue['gender']>;
  disabled: FormControl<AppUserFormRawValue['disabled']>;
  disabledDate: FormControl<AppUserFormRawValue['disabledDate']>;
  user: FormControl<AppUserFormRawValue['user']>;
  company: FormControl<AppUserFormRawValue['company']>;
  permissions: FormControl<AppUserFormRawValue['permissions']>;
};

export type AppUserFormGroup = FormGroup<AppUserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AppUserFormService {
  createAppUserFormGroup(appUser?: AppUserFormGroupInput): AppUserFormGroup {
    const appUserRawValue = this.convertAppUserToAppUserRawValue({
      ...this.getFormDefaults(),
      ...(appUser ?? { id: null }),
    });

    return new FormGroup<AppUserFormGroupContent>({
      id: new FormControl(
        { value: appUserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      phoneNumber: new FormControl(appUserRawValue.phoneNumber, {
        validators: [Validators.required],
      }),
      type: new FormControl(appUserRawValue.type, {
        validators: [Validators.required],
      }),
      birthDate: new FormControl(appUserRawValue.birthDate),
      birthPlace: new FormControl(appUserRawValue.birthPlace),
      gender: new FormControl(appUserRawValue.gender),
      disabled: new FormControl(appUserRawValue.disabled),
      disabledDate: new FormControl(appUserRawValue.disabledDate),
      user: new FormControl(appUserRawValue.user),
      company: new FormControl(appUserRawValue.company),
      permissions: new FormControl(appUserRawValue.permissions ?? []),
    });
  }

  getAppUser(form: AppUserFormGroup): IAppUser | NewAppUser {
    return this.convertAppUserRawValueToAppUser(form.getRawValue());
  }

  resetForm(form: AppUserFormGroup, appUser: AppUserFormGroupInput): void {
    const appUserRawValue = this.convertAppUserToAppUserRawValue({ ...this.getFormDefaults(), ...appUser });
    form.reset({
      ...appUserRawValue,
      id: { value: appUserRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AppUserFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      birthDate: currentTime,
      disabled: false,
      disabledDate: currentTime,
      permissions: [],
    };
  }

  private convertAppUserRawValueToAppUser(rawAppUser: AppUserFormRawValue | NewAppUserFormRawValue): IAppUser | NewAppUser {
    return {
      ...rawAppUser,
      birthDate: dayjs(rawAppUser.birthDate, DATE_TIME_FORMAT),
      disabledDate: dayjs(rawAppUser.disabledDate, DATE_TIME_FORMAT),
    };
  }

  private convertAppUserToAppUserRawValue(
    appUser: IAppUser | (Partial<NewAppUser> & AppUserFormDefaults),
  ): AppUserFormRawValue | PartialWithRequiredKeyOf<NewAppUserFormRawValue> {
    return {
      ...appUser,
      birthDate: appUser.birthDate ? appUser.birthDate.format(DATE_TIME_FORMAT) : undefined,
      disabledDate: appUser.disabledDate ? appUser.disabledDate.format(DATE_TIME_FORMAT) : undefined,
      permissions: appUser.permissions ?? [],
    };
  }
}
