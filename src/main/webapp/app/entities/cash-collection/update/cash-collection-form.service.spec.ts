import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../cash-collection.test-samples';

import { CashCollectionFormService } from './cash-collection-form.service';

describe('CashCollection Form Service', () => {
  let service: CashCollectionFormService;

  beforeEach(() => {
    service = TestBed.inject(CashCollectionFormService);
  });

  describe('Service methods', () => {
    describe('createCashCollectionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCashCollectionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            amount: expect.any(Object),
            company: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing ICashCollection should create a new form with FormGroup', () => {
        const formGroup = service.createCashCollectionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            amount: expect.any(Object),
            company: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getCashCollection', () => {
      it('should return NewCashCollection for default CashCollection initial value', () => {
        const formGroup = service.createCashCollectionFormGroup(sampleWithNewData);

        const cashCollection = service.getCashCollection(formGroup);

        expect(cashCollection).toMatchObject(sampleWithNewData);
      });

      it('should return NewCashCollection for empty CashCollection initial value', () => {
        const formGroup = service.createCashCollectionFormGroup();

        const cashCollection = service.getCashCollection(formGroup);

        expect(cashCollection).toMatchObject({});
      });

      it('should return ICashCollection', () => {
        const formGroup = service.createCashCollectionFormGroup(sampleWithRequiredData);

        const cashCollection = service.getCashCollection(formGroup);

        expect(cashCollection).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICashCollection should not enable id FormControl', () => {
        const formGroup = service.createCashCollectionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCashCollection should disable id FormControl', () => {
        const formGroup = service.createCashCollectionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
