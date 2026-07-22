import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { ICompanyWithOwner, NewCompanyWithOwner } from './company-owner.model';

interface RestCompanyWithOwner {
  company: {
    id: number | null;
    name?: string | null;
    location?: string | null;
    creationDate?: string | null;
    status?: string | null;
  };
  owner: {
    id: number | null;
    userId?: number | null;
    login?: string | null;
    firstName?: string | null;
    lastName?: string | null;
    email?: string | null;
    langKey?: string | null;
    phoneNumber?: string | null;
    type?: string | null;
    birthDate?: string | null;
    birthPlace?: string | null;
    gender?: string | null;
    disabled?: boolean | null;
  };
}

@Injectable({ providedIn: 'root' })
export class CompanyOwnerService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/companies');

  find(id: number): Observable<ICompanyWithOwner> {
    return this.http.get<RestCompanyWithOwner>(`${this.resourceUrl}/${id}/with-owner`).pipe(map(res => this.convertFromServer(res)));
  }

  create(companyWithOwner: NewCompanyWithOwner): Observable<ICompanyWithOwner> {
    return this.http
      .post<RestCompanyWithOwner>(`${this.resourceUrl}/with-owner`, this.convertToServer(companyWithOwner))
      .pipe(map(res => this.convertFromServer(res)));
  }

  update(id: number, companyWithOwner: ICompanyWithOwner): Observable<ICompanyWithOwner> {
    return this.http
      .put<RestCompanyWithOwner>(`${this.resourceUrl}/${id}/with-owner`, this.convertToServer(companyWithOwner))
      .pipe(map(res => this.convertFromServer(res)));
  }

  private convertToServer(companyWithOwner: ICompanyWithOwner | NewCompanyWithOwner): RestCompanyWithOwner {
    return {
      company: {
        ...companyWithOwner.company,
        creationDate: companyWithOwner.company.creationDate?.toJSON() ?? null,
      },
      owner: {
        ...companyWithOwner.owner,
        birthDate: companyWithOwner.owner.birthDate?.toJSON() ?? null,
      },
    };
  }

  private convertFromServer(res: RestCompanyWithOwner): ICompanyWithOwner {
    return {
      company: {
        ...res.company,
        creationDate: res.company.creationDate ? dayjs(res.company.creationDate) : undefined,
      } as ICompanyWithOwner['company'],
      owner: {
        ...res.owner,
        birthDate: res.owner.birthDate ? dayjs(res.owner.birthDate) : undefined,
      } as ICompanyWithOwner['owner'],
    };
  }
}
