import dayjs from 'dayjs/esm';

import { ICompany } from 'app/entities/company/company.model';
import { AppUserTypeEnum } from 'app/entities/enumerations/app-user-type-enum.model';
import { GenderEnum } from 'app/entities/enumerations/gender-enum.model';
import { IPermission } from 'app/entities/permission/permission.model';
import { IUser } from 'app/entities/user/user.model';

export interface IAppUser {
  id: number;
  phoneNumber?: string | null;
  type?: keyof typeof AppUserTypeEnum | null;
  birthDate?: dayjs.Dayjs | null;
  birthPlace?: string | null;
  gender?: keyof typeof GenderEnum | null;
  disabled?: boolean | null;
  disabledDate?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
  company?: Pick<ICompany, 'id'> | null;
  permissions?: Pick<IPermission, 'id'>[] | null;
}

export type NewAppUser = Omit<IAppUser, 'id'> & { id: null };
