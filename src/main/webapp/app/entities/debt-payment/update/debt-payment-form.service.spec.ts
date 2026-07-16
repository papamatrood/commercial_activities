import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../debt-payment.test-samples';

import { DebtPaymentFormService } from './debt-payment-form.service';

describe('DebtPayment Form Service', () => {
  let service: DebtPaymentFormService;

  beforeEach(() => {
    service = TestBed.inject(DebtPaymentFormService);
  });

  describe('Service methods', () => {
    describe('createDebtPaymentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDebtPaymentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            amountPaid: expect.any(Object),
            remainingAmount: expect.any(Object),
            date: expect.any(Object),
            debt: expect.any(Object),
          }),
        );
      });

      it('passing IDebtPayment should create a new form with FormGroup', () => {
        const formGroup = service.createDebtPaymentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            amountPaid: expect.any(Object),
            remainingAmount: expect.any(Object),
            date: expect.any(Object),
            debt: expect.any(Object),
          }),
        );
      });
    });

    describe('getDebtPayment', () => {
      it('should return NewDebtPayment for default DebtPayment initial value', () => {
        const formGroup = service.createDebtPaymentFormGroup(sampleWithNewData);

        const debtPayment = service.getDebtPayment(formGroup);

        expect(debtPayment).toMatchObject(sampleWithNewData);
      });

      it('should return NewDebtPayment for empty DebtPayment initial value', () => {
        const formGroup = service.createDebtPaymentFormGroup();

        const debtPayment = service.getDebtPayment(formGroup);

        expect(debtPayment).toMatchObject({});
      });

      it('should return IDebtPayment', () => {
        const formGroup = service.createDebtPaymentFormGroup(sampleWithRequiredData);

        const debtPayment = service.getDebtPayment(formGroup);

        expect(debtPayment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDebtPayment should not enable id FormControl', () => {
        const formGroup = service.createDebtPaymentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDebtPayment should disable id FormControl', () => {
        const formGroup = service.createDebtPaymentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
