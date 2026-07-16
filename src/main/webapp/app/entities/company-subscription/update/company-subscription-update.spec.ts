import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { ICompanySubscription } from '../company-subscription.model';
import { CompanySubscriptionService } from '../service/company-subscription.service';

import { CompanySubscriptionFormService } from './company-subscription-form.service';
import { CompanySubscriptionUpdate } from './company-subscription-update';

describe('CompanySubscription Management Update Component', () => {
  let comp: CompanySubscriptionUpdate;
  let fixture: ComponentFixture<CompanySubscriptionUpdate>;
  let activatedRoute: ActivatedRoute;
  let companySubscriptionFormService: CompanySubscriptionFormService;
  let companySubscriptionService: CompanySubscriptionService;
  let companyService: CompanyService;

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

    fixture = TestBed.createComponent(CompanySubscriptionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    companySubscriptionFormService = TestBed.inject(CompanySubscriptionFormService);
    companySubscriptionService = TestBed.inject(CompanySubscriptionService);
    companyService = TestBed.inject(CompanyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Company query and add missing value', () => {
      const companySubscription: ICompanySubscription = { id: 3576 };
      const company: ICompany = { id: 29751 };
      companySubscription.company = company;

      const companyCollection: ICompany[] = [{ id: 29751 }];
      vitest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
      const additionalCompanies = [company];
      const expectedCollection: ICompany[] = [...additionalCompanies, ...companyCollection];
      vitest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ companySubscription });
      comp.ngOnInit();

      expect(companyService.query).toHaveBeenCalled();
      expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(
        companyCollection,
        ...additionalCompanies.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.companiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const companySubscription: ICompanySubscription = { id: 3576 };
      const company: ICompany = { id: 29751 };
      companySubscription.company = company;

      activatedRoute.data = of({ companySubscription });
      comp.ngOnInit();

      expect(comp.companiesSharedCollection()).toContainEqual(company);
      expect(comp.companySubscription).toEqual(companySubscription);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICompanySubscription>();
      const companySubscription = { id: 20273 };
      vitest.spyOn(companySubscriptionFormService, 'getCompanySubscription').mockReturnValue(companySubscription);
      vitest.spyOn(companySubscriptionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ companySubscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(companySubscription);
      saveSubject.complete();

      // THEN
      expect(companySubscriptionFormService.getCompanySubscription).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(companySubscriptionService.update).toHaveBeenCalledWith(expect.objectContaining(companySubscription));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICompanySubscription>();
      const companySubscription = { id: 20273 };
      vitest.spyOn(companySubscriptionFormService, 'getCompanySubscription').mockReturnValue({ id: null });
      vitest.spyOn(companySubscriptionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ companySubscription: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(companySubscription);
      saveSubject.complete();

      // THEN
      expect(companySubscriptionFormService.getCompanySubscription).toHaveBeenCalled();
      expect(companySubscriptionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICompanySubscription>();
      const companySubscription = { id: 20273 };
      vitest.spyOn(companySubscriptionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ companySubscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(companySubscriptionService.update).toHaveBeenCalled();
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
  });
});
