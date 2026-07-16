import { IAppUser } from 'app/entities/app-user/app-user.model';

export interface IPermission {
  id: number;
  code?: string | null;
  label?: string | null;
  appUsers?: Pick<IAppUser, 'id'>[] | null;
}

export type NewPermission = Omit<IPermission, 'id'> & { id: null };
