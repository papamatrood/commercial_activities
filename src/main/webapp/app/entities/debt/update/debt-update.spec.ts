import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { IDebt } from '../debt.model';
import { DebtService } from '../service/debt.service';

import { DebtFormService } from './debt-form.service';
import { DebtUpdate } from './debt-update';

describe('Debt Management Update Component', () => {
  let comp: DebtUpdate;
  let fixture: ComponentFixture<DebtUpdate>;
  let activatedRoute: ActivatedRoute;
  let debtFormService: DebtFormService;
  let debtService: DebtService;
  let companyService: CompanyService;
  let saleService: SaleService;

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

    fixture = TestBed.createComponent(DebtUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    debtFormService = TestBed.inject(DebtFormService);
    debtService = TestBed.inject(DebtService);
    companyService = TestBed.inject(CompanyService);
    saleService = TestBed.inject(SaleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Company query and add missing value', () => {
      const debt: IDebt = { id: 12844 };
      const company: ICompany = { id: 29751 };
      debt.company = company;

      const companyCollection: ICompany[] = [{ id: 29751 }];
      vitest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
      const additionalCompanies = [company];
      const expectedCollection: ICompany[] = [...additionalCompanies, ...companyCollection];
      vitest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ debt });
      comp.ngOnInit();

      expect(companyService.query).toHaveBeenCalled();
      expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(
        companyCollection,
        ...additionalCompanies.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.companiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Sale query and add missing value', () => {
      const debt: IDebt = { id: 12844 };
      const sale: ISale = { id: 2908 };
      debt.sale = sale;

      const saleCollection: ISale[] = [{ id: 2908 }];
      vitest.spyOn(saleService, 'query').mockReturnValue(of(new HttpResponse({ body: saleCollection })));
      const additionalSales = [sale];
      const expectedCollection: ISale[] = [...additionalSales, ...saleCollection];
      vitest.spyOn(saleService, 'addSaleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ debt });
      comp.ngOnInit();

      expect(saleService.query).toHaveBeenCalled();
      expect(saleService.addSaleToCollectionIfMissing).toHaveBeenCalledWith(
        saleCollection,
        ...additionalSales.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.salesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const debt: IDebt = { id: 12844 };
      const company: ICompany = { id: 29751 };
      debt.company = company;
      const sale: ISale = { id: 2908 };
      debt.sale = sale;

      activatedRoute.data = of({ debt });
      comp.ngOnInit();

      expect(comp.companiesSharedCollection()).toContainEqual(company);
      expect(comp.salesSharedCollection()).toContainEqual(sale);
      expect(comp.debt).toEqual(debt);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDebt>();
      const debt = { id: 21142 };
      vitest.spyOn(debtFormService, 'getDebt').mockReturnValue(debt);
      vitest.spyOn(debtService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ debt });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(debt);
      saveSubject.complete();

      // THEN
      expect(debtFormService.getDebt).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(debtService.update).toHaveBeenCalledWith(expect.objectContaining(debt));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDebt>();
      const debt = { id: 21142 };
      vitest.spyOn(debtFormService, 'getDebt').mockReturnValue({ id: null });
      vitest.spyOn(debtService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ debt: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(debt);
      saveSubject.complete();

      // THEN
      expect(debtFormService.getDebt).toHaveBeenCalled();
      expect(debtService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDebt>();
      const debt = { id: 21142 };
      vitest.spyOn(debtService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ debt });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(debtService.update).toHaveBeenCalled();
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

    describe('compareSale', () => {
      it('should forward to saleService', () => {
        const entity = { id: 2908 };
        const entity2 = { id: 10270 };
        vitest.spyOn(saleService, 'compareSale');
        comp.compareSale(entity, entity2);
        expect(saleService.compareSale).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
