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
import { ICashDisbursement } from '../cash-disbursement.model';
import { CashDisbursementService } from '../service/cash-disbursement.service';

import { CashDisbursementFormService } from './cash-disbursement-form.service';
import { CashDisbursementUpdate } from './cash-disbursement-update';

describe('CashDisbursement Management Update Component', () => {
  let comp: CashDisbursementUpdate;
  let fixture: ComponentFixture<CashDisbursementUpdate>;
  let activatedRoute: ActivatedRoute;
  let cashDisbursementFormService: CashDisbursementFormService;
  let cashDisbursementService: CashDisbursementService;
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

    fixture = TestBed.createComponent(CashDisbursementUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cashDisbursementFormService = TestBed.inject(CashDisbursementFormService);
    cashDisbursementService = TestBed.inject(CashDisbursementService);
    companyService = TestBed.inject(CompanyService);
    appUserService = TestBed.inject(AppUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Company query and add missing value', () => {
      const cashDisbursement: ICashDisbursement = { id: 2526 };
      const company: ICompany = { id: 29751 };
      cashDisbursement.company = company;

      const companyCollection: ICompany[] = [{ id: 29751 }];
      vitest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
      const additionalCompanies = [company];
      const expectedCollection: ICompany[] = [...additionalCompanies, ...companyCollection];
      vitest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cashDisbursement });
      comp.ngOnInit();

      expect(companyService.query).toHaveBeenCalled();
      expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(
        companyCollection,
        ...additionalCompanies.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.companiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call AppUser query and add missing value', () => {
      const cashDisbursement: ICashDisbursement = { id: 2526 };
      const user: IAppUser = { id: 14418 };
      cashDisbursement.user = user;

      const appUserCollection: IAppUser[] = [{ id: 14418 }];
      vitest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [user];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      vitest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cashDisbursement });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.appUsersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const cashDisbursement: ICashDisbursement = { id: 2526 };
      const company: ICompany = { id: 29751 };
      cashDisbursement.company = company;
      const user: IAppUser = { id: 14418 };
      cashDisbursement.user = user;

      activatedRoute.data = of({ cashDisbursement });
      comp.ngOnInit();

      expect(comp.companiesSharedCollection()).toContainEqual(company);
      expect(comp.appUsersSharedCollection()).toContainEqual(user);
      expect(comp.cashDisbursement).toEqual(cashDisbursement);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICashDisbursement>();
      const cashDisbursement = { id: 16878 };
      vitest.spyOn(cashDisbursementFormService, 'getCashDisbursement').mockReturnValue(cashDisbursement);
      vitest.spyOn(cashDisbursementService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashDisbursement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(cashDisbursement);
      saveSubject.complete();

      // THEN
      expect(cashDisbursementFormService.getCashDisbursement).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cashDisbursementService.update).toHaveBeenCalledWith(expect.objectContaining(cashDisbursement));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICashDisbursement>();
      const cashDisbursement = { id: 16878 };
      vitest.spyOn(cashDisbursementFormService, 'getCashDisbursement').mockReturnValue({ id: null });
      vitest.spyOn(cashDisbursementService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashDisbursement: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(cashDisbursement);
      saveSubject.complete();

      // THEN
      expect(cashDisbursementFormService.getCashDisbursement).toHaveBeenCalled();
      expect(cashDisbursementService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICashDisbursement>();
      const cashDisbursement = { id: 16878 };
      vitest.spyOn(cashDisbursementService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashDisbursement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cashDisbursementService.update).toHaveBeenCalled();
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
