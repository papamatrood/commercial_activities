import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../cash-disbursement.test-samples';

import { CashDisbursementFormService } from './cash-disbursement-form.service';

describe('CashDisbursement Form Service', () => {
  let service: CashDisbursementFormService;

  beforeEach(() => {
    service = TestBed.inject(CashDisbursementFormService);
  });

  describe('Service methods', () => {
    describe('createCashDisbursementFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCashDisbursementFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reason: expect.any(Object),
            amount: expect.any(Object),
            date: expect.any(Object),
            company: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing ICashDisbursement should create a new form with FormGroup', () => {
        const formGroup = service.createCashDisbursementFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reason: expect.any(Object),
            amount: expect.any(Object),
            date: expect.any(Object),
            company: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getCashDisbursement', () => {
      it('should return NewCashDisbursement for default CashDisbursement initial value', () => {
        const formGroup = service.createCashDisbursementFormGroup(sampleWithNewData);

        const cashDisbursement = service.getCashDisbursement(formGroup);

        expect(cashDisbursement).toMatchObject(sampleWithNewData);
      });

      it('should return NewCashDisbursement for empty CashDisbursement initial value', () => {
        const formGroup = service.createCashDisbursementFormGroup();

        const cashDisbursement = service.getCashDisbursement(formGroup);

        expect(cashDisbursement).toMatchObject({});
      });

      it('should return ICashDisbursement', () => {
        const formGroup = service.createCashDisbursementFormGroup(sampleWithRequiredData);

        const cashDisbursement = service.getCashDisbursement(formGroup);

        expect(cashDisbursement).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICashDisbursement should not enable id FormControl', () => {
        const formGroup = service.createCashDisbursementFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCashDisbursement should disable id FormControl', () => {
        const formGroup = service.createCashDisbursementFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
