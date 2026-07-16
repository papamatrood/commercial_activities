import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICompanySubscription, NewCompanySubscription } from '../company-subscription.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICompanySubscription for edit and NewCompanySubscriptionFormGroupInput for create.
 */
type CompanySubscriptionFormGroupInput = ICompanySubscription | PartialWithRequiredKeyOf<NewCompanySubscription>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICompanySubscription | NewCompanySubscription> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

type CompanySubscriptionFormRawValue = FormValueOf<ICompanySubscription>;

type NewCompanySubscriptionFormRawValue = FormValueOf<NewCompanySubscription>;

type CompanySubscriptionFormDefaults = Pick<NewCompanySubscription, 'id' | 'startDate' | 'endDate'>;

type CompanySubscriptionFormGroupContent = {
  id: FormControl<CompanySubscriptionFormRawValue['id'] | NewCompanySubscription['id']>;
  type: FormControl<CompanySubscriptionFormRawValue['type']>;
  startDate: FormControl<CompanySubscriptionFormRawValue['startDate']>;
  endDate: FormControl<CompanySubscriptionFormRawValue['endDate']>;
  status: FormControl<CompanySubscriptionFormRawValue['status']>;
  company: FormControl<CompanySubscriptionFormRawValue['company']>;
};

export type CompanySubscriptionFormGroup = FormGroup<CompanySubscriptionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CompanySubscriptionFormService {
  createCompanySubscriptionFormGroup(companySubscription?: CompanySubscriptionFormGroupInput): CompanySubscriptionFormGroup {
    const companySubscriptionRawValue = this.convertCompanySubscriptionToCompanySubscriptionRawValue({
      ...this.getFormDefaults(),
      ...(companySubscription ?? { id: null }),
    });

    return new FormGroup<CompanySubscriptionFormGroupContent>({
      id: new FormControl(
        { value: companySubscriptionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      type: new FormControl(companySubscriptionRawValue.type, {
        validators: [Validators.required],
      }),
      startDate: new FormControl(companySubscriptionRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(companySubscriptionRawValue.endDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(companySubscriptionRawValue.status),
      company: new FormControl(companySubscriptionRawValue.company),
    });
  }

  getCompanySubscription(form: CompanySubscriptionFormGroup): ICompanySubscription | NewCompanySubscription {
    return this.convertCompanySubscriptionRawValueToCompanySubscription(form.getRawValue());
  }

  resetForm(form: CompanySubscriptionFormGroup, companySubscription: CompanySubscriptionFormGroupInput): void {
    const companySubscriptionRawValue = this.convertCompanySubscriptionToCompanySubscriptionRawValue({
      ...this.getFormDefaults(),
      ...companySubscription,
    });
    form.reset({
      ...companySubscriptionRawValue,
      id: { value: companySubscriptionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CompanySubscriptionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDate: currentTime,
      endDate: currentTime,
    };
  }

  private convertCompanySubscriptionRawValueToCompanySubscription(
    rawCompanySubscription: CompanySubscriptionFormRawValue | NewCompanySubscriptionFormRawValue,
  ): ICompanySubscription | NewCompanySubscription {
    return {
      ...rawCompanySubscription,
      startDate: dayjs(rawCompanySubscription.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawCompanySubscription.endDate, DATE_TIME_FORMAT),
    };
  }

  private convertCompanySubscriptionToCompanySubscriptionRawValue(
    companySubscription: ICompanySubscription | (Partial<NewCompanySubscription> & CompanySubscriptionFormDefaults),
  ): CompanySubscriptionFormRawValue | PartialWithRequiredKeyOf<NewCompanySubscriptionFormRawValue> {
    return {
      ...companySubscription,
      startDate: companySubscription.startDate ? companySubscription.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: companySubscription.endDate ? companySubscription.endDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
