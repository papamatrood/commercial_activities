import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sale-line.test-samples';

import { SaleLineFormService } from './sale-line-form.service';

describe('SaleLine Form Service', () => {
  let service: SaleLineFormService;

  beforeEach(() => {
    service = TestBed.inject(SaleLineFormService);
  });

  describe('Service methods', () => {
    describe('createSaleLineFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSaleLineFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            barcode: expect.any(Object),
            quantity: expect.any(Object),
            unitPrice: expect.any(Object),
            totalPrice: expect.any(Object),
            sale: expect.any(Object),
            product: expect.any(Object),
          }),
        );
      });

      it('passing ISaleLine should create a new form with FormGroup', () => {
        const formGroup = service.createSaleLineFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            barcode: expect.any(Object),
            quantity: expect.any(Object),
            unitPrice: expect.any(Object),
            totalPrice: expect.any(Object),
            sale: expect.any(Object),
            product: expect.any(Object),
          }),
        );
      });
    });

    describe('getSaleLine', () => {
      it('should return NewSaleLine for default SaleLine initial value', () => {
        const formGroup = service.createSaleLineFormGroup(sampleWithNewData);

        const saleLine = service.getSaleLine(formGroup);

        expect(saleLine).toMatchObject(sampleWithNewData);
      });

      it('should return NewSaleLine for empty SaleLine initial value', () => {
        const formGroup = service.createSaleLineFormGroup();

        const saleLine = service.getSaleLine(formGroup);

        expect(saleLine).toMatchObject({});
      });

      it('should return ISaleLine', () => {
        const formGroup = service.createSaleLineFormGroup(sampleWithRequiredData);

        const saleLine = service.getSaleLine(formGroup);

        expect(saleLine).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISaleLine should not enable id FormControl', () => {
        const formGroup = service.createSaleLineFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSaleLine should disable id FormControl', () => {
        const formGroup = service.createSaleLineFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
