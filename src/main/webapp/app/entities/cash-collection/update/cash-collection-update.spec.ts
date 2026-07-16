import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';
import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { ICashCollection } from '../cash-collection.model';
import { CashCollectionService } from '../service/cash-collection.service';

import { CashCollectionFormService } from './cash-collection-form.service';
import { CashCollectionUpdate } from './cash-collection-update';

describe('CashCollection Management Update Component', () => {
  let comp: CashCollectionUpdate;
  let fixture: ComponentFixture<CashCollectionUpdate>;
  let activatedRoute: ActivatedRoute;
  let cashCollectionFormService: CashCollectionFormService;
  let cashCollectionService: CashCollectionService;
  let companyService: CompanyService;
  let appUserService: AppUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(CashCollectionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cashCollectionFormService = TestBed.inject(CashCollectionFormService);
    cashCollectionService = TestBed.inject(CashCollectionService);
    companyService = TestBed.inject(CompanyService);
    appUserService = TestBed.inject(AppUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Company query and add missing value', () => {
      const cashCollection: ICashCollection = { id: 5994 };
      const company: ICompany = { id: 29751 };
      cashCollection.company = company;

      const companyCollection: ICompany[] = [{ id: 29751 }];
      vitest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
      const additionalCompanies = [company];
      const expectedCollection: ICompany[] = [...additionalCompanies, ...companyCollection];
      vitest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cashCollection });
      comp.ngOnInit();

      expect(companyService.query).toHaveBeenCalled();
      expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(
        companyCollection,
        ...additionalCompanies.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.companiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call AppUser query and add missing value', () => {
      const cashCollection: ICashCollection = { id: 5994 };
      const user: IAppUser = { id: 14418 };
      cashCollection.user = user;

      const appUserCollection: IAppUser[] = [{ id: 14418 }];
      vitest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [user];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      vitest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cashCollection });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.appUsersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const cashCollection: ICashCollection = { id: 5994 };
      const company: ICompany = { id: 29751 };
      cashCollection.company = company;
      const user: IAppUser = { id: 14418 };
      cashCollection.user = user;

      activatedRoute.data = of({ cashCollection });
      comp.ngOnInit();

      expect(comp.companiesSharedCollection()).toContainEqual(company);
      expect(comp.appUsersSharedCollection()).toContainEqual(user);
      expect(comp.cashCollection).toEqual(cashCollection);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICashCollection>();
      const cashCollection = { id: 8031 };
      vitest.spyOn(cashCollectionFormService, 'getCashCollection').mockReturnValue(cashCollection);
      vitest.spyOn(cashCollectionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashCollection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(cashCollection);
      saveSubject.complete();

      // THEN
      expect(cashCollectionFormService.getCashCollection).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cashCollectionService.update).toHaveBeenCalledWith(expect.objectContaining(cashCollection));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICashCollection>();
      const cashCollection = { id: 8031 };
      vitest.spyOn(cashCollectionFormService, 'getCashCollection').mockReturnValue({ id: null });
      vitest.spyOn(cashCollectionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashCollection: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(cashCollection);
      saveSubject.complete();

      // THEN
      expect(cashCollectionFormService.getCashCollection).toHaveBeenCalled();
      expect(cashCollectionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICashCollection>();
      const cashCollection = { id: 8031 };
      vitest.spyOn(cashCollectionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashCollection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cashCollectionService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCompany', () => {
      it('should forward to companyService', () => {
        const entity = { id: 29751 };
        const entity2 = { id: 7586 };
        vitest.spyOn(companyService, 'compareCompany');
        comp.compareCompany(entity, entity2);
        expect(companyService.compareCompany).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareAppUser', () => {
      it('should forward to appUserService', () => {
        const entity = { id: 14418 };
        const entity2 = { id: 16679 };
        vitest.spyOn(appUserService, 'compareAppUser');
        comp.compareAppUser(entity, entity2);
        expect(appUserService.compareAppUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
