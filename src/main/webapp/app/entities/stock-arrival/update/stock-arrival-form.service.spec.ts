import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../stock-arrival.test-samples';

import { StockArrivalFormService } from './stock-arrival-form.service';

describe('StockArrival Form Service', () => {
  let service: StockArrivalFormService;

  beforeEach(() => {
    service = TestBed.inject(StockArrivalFormService);
  });

  describe('Service methods', () => {
    describe('createStockArrivalFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStockArrivalFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            barcode: expect.any(Object),
            quantity: expect.any(Object),
            amount: expect.any(Object),
            date: expect.any(Object),
            product: expect.any(Object),
          }),
        );
      });

      it('passing IStockArrival should create a new form with FormGroup', () => {
        const formGroup = service.createStockArrivalFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            barcode: expect.any(Object),
            quantity: expect.any(Object),
            amount: expect.any(Object),
            date: expect.any(Object),
            product: expect.any(Object),
          }),
        );
      });
    });

    describe('getStockArrival', () => {
      it('should return NewStockArrival for default StockArrival initial value', () => {
        const formGroup = service.createStockArrivalFormGroup(sampleWithNewData);

        const stockArrival = service.getStockArrival(formGroup);

        expect(stockArrival).toMatchObject(sampleWithNewData);
      });

      it('should return NewStockArrival for empty StockArrival initial value', () => {
        const formGroup = service.createStockArrivalFormGroup();

        const stockArrival = service.getStockArrival(formGroup);

        expect(stockArrival).toMatchObject({});
      });

      it('should return IStockArrival', () => {
        const formGroup = service.createStockArrivalFormGroup(sampleWithRequiredData);

        const stockArrival = service.getStockArrival(formGroup);

        expect(stockArrival).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStockArrival should not enable id FormControl', () => {
        const formGroup = service.createStockArrivalFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStockArrival should disable id FormControl', () => {
        const formGroup = service.createStockArrivalFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
