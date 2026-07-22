import dayjs from 'dayjs/esm';

import { ICompany, NewCompany } from 'app/entities/company/company.model';
import { AppUserTypeEnum } from 'app/entities/enumerations/app-user-type-enum.model';
import { GenderEnum } from 'app/entities/enumerations/gender-enum.model';

export interface ICompanyOwner {
  id: number | null;
  userId?: number | null;
  login?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  langKey?: string | null;
  phoneNumber?: string | null;
  type?: keyof typeof AppUserTypeEnum | null;
  birthDate?: dayjs.Dayjs | null;
  birthPlace?: string | null;
  gender?: keyof typeof GenderEnum | null;
  disabled?: boolean | null;
}

export type NewCompanyOwner = Omit<ICompanyOwner, 'id'> & { id: null };

export interface ICompanyWithOwner {
  company: ICompany;
  owner: ICompanyOwner;
}

export interface NewCompanyWithOwner {
  company: NewCompany;
  owner: NewCompanyOwner;
}
