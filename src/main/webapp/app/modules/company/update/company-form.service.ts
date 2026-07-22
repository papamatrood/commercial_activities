import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ICompanyOwner, ICompanyWithOwner, NewCompanyWithOwner } from '../company-owner.model';

type CompanyFormGroupInput = ICompanyWithOwner | NewCompanyWithOwner;

type CompanyDetailsFormGroupContent = {
  id: FormControl<number | null>;
  name: FormControl<string | null | undefined>;
  location: FormControl<string | null | undefined>;
  creationDate: FormControl<string | null>;
  status: FormControl<ICompanyWithOwner['company']['status']>;
};

type CompanyOwnerFormGroupContent = {
  id: FormControl<number | null>;
  login: FormControl<string | null | undefined>;
  firstName: FormControl<string | null | undefined>;
  lastName: FormControl<string | null | undefined>;
  phoneNumber: FormControl<string | null | undefined>;
  email: FormControl<string | null | undefined>;
  langKey: FormControl<string | null | undefined>;
  birthDate: FormControl<string | null>;
  birthPlace: FormControl<string | null | undefined>;
  gender: FormControl<ICompanyOwner['gender']>;
  type: FormControl<ICompanyOwner['type']>;
  disabled: FormControl<boolean | null | undefined>;
};

type CompanyFormGroupContent = {
  company: FormGroup<CompanyDetailsFormGroupContent>;
  owner: FormGroup<CompanyOwnerFormGroupContent>;
};

export type CompanyFormGroup = FormGroup<CompanyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CompanyFormService {
  createCompanyFormGroup(companyWithOwner?: CompanyFormGroupInput): CompanyFormGroup {
    const company = companyWithOwner?.company;
    const owner = companyWithOwner?.owner;

    return new FormGroup<CompanyFormGroupContent>({
      company: new FormGroup<CompanyDetailsFormGroupContent>({
        id: new FormControl({ value: company?.id ?? null, disabled: true }, { nonNullable: true }),
        name: new FormControl(company?.name, {
          validators: [Validators.required],
        }),
        location: new FormControl(company?.location),
        creationDate: new FormControl(this.formatDate(company?.creationDate ?? dayjs())),
        status: new FormControl(company?.status),
      }),
      owner: new FormGroup<CompanyOwnerFormGroupContent>({
        id: new FormControl({ value: owner?.id ?? null, disabled: true }, { nonNullable: true }),
        login: new FormControl(owner?.login, {
          validators: [Validators.required],
        }),
        firstName: new FormControl(owner?.firstName),
        lastName: new FormControl(owner?.lastName),
        phoneNumber: new FormControl(owner?.phoneNumber, {
          validators: [Validators.required],
        }),
        email: new FormControl(owner?.email, {
          validators: [Validators.email],
        }),
        langKey: new FormControl(owner?.langKey ?? 'fr'),
        birthDate: new FormControl(this.formatDate(owner?.birthDate)),
        birthPlace: new FormControl(owner?.birthPlace),
        gender: new FormControl(owner?.gender),
        type: new FormControl(owner?.type ?? 'COMPANY_ADMIN', {
          validators: [Validators.required],
        }),
        disabled: new FormControl(owner?.disabled ?? false),
      }),
    });
  }

  getCompanyWithOwner(form: CompanyFormGroup): ICompanyWithOwner | NewCompanyWithOwner {
    const rawValue = form.getRawValue();
    return {
      company: {
        ...rawValue.company,
        creationDate: this.parseDate(rawValue.company.creationDate),
      },
      owner: {
        ...rawValue.owner,
        birthDate: this.parseDate(rawValue.owner.birthDate),
      },
    } as ICompanyWithOwner | NewCompanyWithOwner;
  }

  resetForm(form: CompanyFormGroup, companyWithOwner: CompanyFormGroupInput): void {
    form.reset({
      company: {
        ...companyWithOwner.company,
        id: companyWithOwner.company.id,
        creationDate: this.formatDate(companyWithOwner.company.creationDate),
      },
      owner: {
        ...companyWithOwner.owner,
        id: companyWithOwner.owner.id,
        birthDate: this.formatDate(companyWithOwner.owner.birthDate),
      },
    });
  }

  private formatDate(value?: dayjs.Dayjs | null): string | null {
    return value ? value.format(DATE_TIME_FORMAT) : null;
  }

  private parseDate(value?: string | null): dayjs.Dayjs | null {
    return value ? dayjs(value, DATE_TIME_FORMAT) : null;
  }
}
