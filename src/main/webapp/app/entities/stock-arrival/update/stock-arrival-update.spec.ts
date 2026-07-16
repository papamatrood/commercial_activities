import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { StockArrivalService } from '../service/stock-arrival.service';
import { IStockArrival } from '../stock-arrival.model';

import { StockArrivalFormService } from './stock-arrival-form.service';
import { StockArrivalUpdate } from './stock-arrival-update';

describe('StockArrival Management Update Component', () => {
  let comp: StockArrivalUpdate;
  let fixture: ComponentFixture<StockArrivalUpdate>;
  let activatedRoute: ActivatedRoute;
  let stockArrivalFormService: StockArrivalFormService;
  let stockArrivalService: StockArrivalService;
  let productService: ProductService;

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

    fixture = TestBed.createComponent(StockArrivalUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stockArrivalFormService = TestBed.inject(StockArrivalFormService);
    stockArrivalService = TestBed.inject(StockArrivalService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Product query and add missing value', () => {
      const stockArrival: IStockArrival = { id: 6242 };
      const product: IProduct = { id: 21536 };
      stockArrival.product = product;

      const productCollection: IProduct[] = [{ id: 21536 }];
      vitest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      vitest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stockArrival });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.productsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const stockArrival: IStockArrival = { id: 6242 };
      const product: IProduct = { id: 21536 };
      stockArrival.product = product;

      activatedRoute.data = of({ stockArrival });
      comp.ngOnInit();

      expect(comp.productsSharedCollection()).toContainEqual(product);
      expect(comp.stockArrival).toEqual(stockArrival);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStockArrival>();
      const stockArrival = { id: 5003 };
      vitest.spyOn(stockArrivalFormService, 'getStockArrival').mockReturnValue(stockArrival);
      vitest.spyOn(stockArrivalService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockArrival });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(stockArrival);
      saveSubject.complete();

      // THEN
      expect(stockArrivalFormService.getStockArrival).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stockArrivalService.update).toHaveBeenCalledWith(expect.objectContaining(stockArrival));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStockArrival>();
      const stockArrival = { id: 5003 };
      vitest.spyOn(stockArrivalFormService, 'getStockArrival').mockReturnValue({ id: null });
      vitest.spyOn(stockArrivalService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockArrival: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(stockArrival);
      saveSubject.complete();

      // THEN
      expect(stockArrivalFormService.getStockArrival).toHaveBeenCalled();
      expect(stockArrivalService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IStockArrival>();
      const stockArrival = { id: 5003 };
      vitest.spyOn(stockArrivalService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockArrival });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stockArrivalService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProduct', () => {
      it('should forward to productService', () => {
        const entity = { id: 21536 };
        const entity2 = { id: 11926 };
        vitest.spyOn(productService, 'compareProduct');
        comp.compareProduct(entity, entity2);
        expect(productService.compareProduct).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
