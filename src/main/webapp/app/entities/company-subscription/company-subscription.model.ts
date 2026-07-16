import dayjs from 'dayjs/esm';

import { ICompany } from 'app/entities/company/company.model';
import { CompanySubscriptionStatusEnum } from 'app/entities/enumerations/company-subscription-status-enum.model';
import { CompanySubscriptionTypeEnum } from 'app/entities/enumerations/company-subscription-type-enum.model';

export interface ICompanySubscription {
  id: number;
  type?: keyof typeof CompanySubscriptionTypeEnum | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  status?: keyof typeof CompanySubscriptionStatusEnum | null;
  company?: Pick<ICompany, 'id'> | null;
}

export type NewCompanySubscription = Omit<ICompanySubscription, 'id'> & { id: null };
