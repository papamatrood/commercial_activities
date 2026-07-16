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
import { ISale } from '../sale.model';
import { SaleService } from '../service/sale.service';

import { SaleFormService } from './sale-form.service';
import { SaleUpdate } from './sale-update';

describe('Sale Management Update Component', () => {
  let comp: SaleUpdate;
  let fixture: ComponentFixture<SaleUpdate>;
  let activatedRoute: ActivatedRoute;
  let saleFormService: SaleFormService;
  let saleService: SaleService;
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

    fixture = TestBed.createComponent(SaleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    saleFormService = TestBed.inject(SaleFormService);
    saleService = TestBed.inject(SaleService);
    companyService = TestBed.inject(CompanyService);
    appUserService = TestBed.inject(AppUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Company query and add missing value', () => {
      const sale: ISale = { id: 10270 };
      const company: ICompany = { id: 29751 };
      sale.company = company;

      const companyCollection: ICompany[] = [{ id: 29751 }];
      vitest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
      const additionalCompanies = [company];
      const expectedCollection: ICompany[] = [...additionalCompanies, ...companyCollection];
      vitest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sale });
      comp.ngOnInit();

      expect(companyService.query).toHaveBeenCalled();
      expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(
        companyCollection,
        ...additionalCompanies.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.companiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call AppUser query and add missing value', () => {
      const sale: ISale = { id: 10270 };
      const seller: IAppUser = { id: 14418 };
      sale.seller = seller;

      const appUserCollection: IAppUser[] = [{ id: 14418 }];
      vitest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [seller];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      vitest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sale });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.appUsersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const sale: ISale = { id: 10270 };
      const company: ICompany = { id: 29751 };
      sale.company = company;
      const seller: IAppUser = { id: 14418 };
      sale.seller = seller;

      activatedRoute.data = of({ sale });
      comp.ngOnInit();

      expect(comp.companiesSharedCollection()).toContainEqual(company);
      expect(comp.appUsersSharedCollection()).toContainEqual(seller);
      expect(comp.sale).toEqual(sale);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISale>();
      const sale = { id: 2908 };
      vitest.spyOn(saleFormService, 'getSale').mockReturnValue(sale);
      vitest.spyOn(saleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sale });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(sale);
      saveSubject.complete();

      // THEN
      expect(saleFormService.getSale).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(saleService.update).toHaveBeenCalledWith(expect.objectContaining(sale));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISale>();
      const sale = { id: 2908 };
      vitest.spyOn(saleFormService, 'getSale').mockReturnValue({ id: null });
      vitest.spyOn(saleService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sale: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(sale);
      saveSubject.complete();

      // THEN
      expect(saleFormService.getSale).toHaveBeenCalled();
      expect(saleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISale>();
      const sale = { id: 2908 };
      vitest.spyOn(saleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sale });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(saleService.update).toHaveBeenCalled();
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
