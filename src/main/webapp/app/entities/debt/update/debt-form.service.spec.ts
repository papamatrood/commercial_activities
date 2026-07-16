import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../debt.test-samples';

import { DebtFormService } from './debt-form.service';

describe('Debt Form Service', () => {
  let service: DebtFormService;

  beforeEach(() => {
    service = TestBed.inject(DebtFormService);
  });

  describe('Service methods', () => {
    describe('createDebtFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDebtFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            totalAmount: expect.any(Object),
            amountPaid: expect.any(Object),
            remainingAmount: expect.any(Object),
            date: expect.any(Object),
            company: expect.any(Object),
            sale: expect.any(Object),
          }),
        );
      });

      it('passing IDebt should create a new form with FormGroup', () => {
        const formGroup = service.createDebtFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            totalAmount: expect.any(Object),
            amountPaid: expect.any(Object),
            remainingAmount: expect.any(Object),
            date: expect.any(Object),
            company: expect.any(Object),
            sale: expect.any(Object),
          }),
        );
      });
    });

    describe('getDebt', () => {
      it('should return NewDebt for default Debt initial value', () => {
        const formGroup = service.createDebtFormGroup(sampleWithNewData);

        const debt = service.getDebt(formGroup);

        expect(debt).toMatchObject(sampleWithNewData);
      });

      it('should return NewDebt for empty Debt initial value', () => {
        const formGroup = service.createDebtFormGroup();

        const debt = service.getDebt(formGroup);

        expect(debt).toMatchObject({});
      });

      it('should return IDebt', () => {
        const formGroup = service.createDebtFormGroup(sampleWithRequiredData);

        const debt = service.getDebt(formGroup);

        expect(debt).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDebt should not enable id FormControl', () => {
        const formGroup = service.createDebtFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDebt should disable id FormControl', () => {
        const formGroup = service.createDebtFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
