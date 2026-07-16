import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDebt } from 'app/entities/debt/debt.model';
import { DebtService } from 'app/entities/debt/service/debt.service';
import { IDebtPayment } from '../debt-payment.model';
import { DebtPaymentService } from '../service/debt-payment.service';

import { DebtPaymentFormService } from './debt-payment-form.service';
import { DebtPaymentUpdate } from './debt-payment-update';

describe('DebtPayment Management Update Component', () => {
  let comp: DebtPaymentUpdate;
  let fixture: ComponentFixture<DebtPaymentUpdate>;
  let activatedRoute: ActivatedRoute;
  let debtPaymentFormService: DebtPaymentFormService;
  let debtPaymentService: DebtPaymentService;
  let debtService: DebtService;

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

    fixture = TestBed.createComponent(DebtPaymentUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    debtPaymentFormService = TestBed.inject(DebtPaymentFormService);
    debtPaymentService = TestBed.inject(DebtPaymentService);
    debtService = TestBed.inject(DebtService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Debt query and add missing value', () => {
      const debtPayment: IDebtPayment = { id: 1541 };
      const debt: IDebt = { id: 21142 };
      debtPayment.debt = debt;

      const debtCollection: IDebt[] = [{ id: 21142 }];
      vitest.spyOn(debtService, 'query').mockReturnValue(of(new HttpResponse({ body: debtCollection })));
      const additionalDebts = [debt];
      const expectedCollection: IDebt[] = [...additionalDebts, ...debtCollection];
      vitest.spyOn(debtService, 'addDebtToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ debtPayment });
      comp.ngOnInit();

      expect(debtService.query).toHaveBeenCalled();
      expect(debtService.addDebtToCollectionIfMissing).toHaveBeenCalledWith(
        debtCollection,
        ...additionalDebts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.debtsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const debtPayment: IDebtPayment = { id: 1541 };
      const debt: IDebt = { id: 21142 };
      debtPayment.debt = debt;

      activatedRoute.data = of({ debtPayment });
      comp.ngOnInit();

      expect(comp.debtsSharedCollection()).toContainEqual(debt);
      expect(comp.debtPayment).toEqual(debtPayment);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDebtPayment>();
      const debtPayment = { id: 942 };
      vitest.spyOn(debtPaymentFormService, 'getDebtPayment').mockReturnValue(debtPayment);
      vitest.spyOn(debtPaymentService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ debtPayment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(debtPayment);
      saveSubject.complete();

      // THEN
      expect(debtPaymentFormService.getDebtPayment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(debtPaymentService.update).toHaveBeenCalledWith(expect.objectContaining(debtPayment));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDebtPayment>();
      const debtPayment = { id: 942 };
      vitest.spyOn(debtPaymentFormService, 'getDebtPayment').mockReturnValue({ id: null });
      vitest.spyOn(debtPaymentService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ debtPayment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(debtPayment);
      saveSubject.complete();

      // THEN
      expect(debtPaymentFormService.getDebtPayment).toHaveBeenCalled();
      expect(debtPaymentService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDebtPayment>();
      const debtPayment = { id: 942 };
      vitest.spyOn(debtPaymentService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ debtPayment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(debtPaymentService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDebt', () => {
      it('should forward to debtService', () => {
        const entity = { id: 21142 };
        const entity2 = { id: 12844 };
        vitest.spyOn(debtService, 'compareDebt');
        comp.compareDebt(entity, entity2);
        expect(debtService.compareDebt).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
