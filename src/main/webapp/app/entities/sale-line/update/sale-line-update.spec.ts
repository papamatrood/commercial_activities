import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { ISaleLine } from '../sale-line.model';
import { SaleLineService } from '../service/sale-line.service';

import { SaleLineFormService } from './sale-line-form.service';
import { SaleLineUpdate } from './sale-line-update';

describe('SaleLine Management Update Component', () => {
  let comp: SaleLineUpdate;
  let fixture: ComponentFixture<SaleLineUpdate>;
  let activatedRoute: ActivatedRoute;
  let saleLineFormService: SaleLineFormService;
  let saleLineService: SaleLineService;
  let saleService: SaleService;
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

    fixture = TestBed.createComponent(SaleLineUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    saleLineFormService = TestBed.inject(SaleLineFormService);
    saleLineService = TestBed.inject(SaleLineService);
    saleService = TestBed.inject(SaleService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Sale query and add missing value', () => {
      const saleLine: ISaleLine = { id: 10851 };
      const sale: ISale = { id: 2908 };
      saleLine.sale = sale;

      const saleCollection: ISale[] = [{ id: 2908 }];
      vitest.spyOn(saleService, 'query').mockReturnValue(of(new HttpResponse({ body: saleCollection })));
      const additionalSales = [sale];
      const expectedCollection: ISale[] = [...additionalSales, ...saleCollection];
      vitest.spyOn(saleService, 'addSaleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ saleLine });
      comp.ngOnInit();

      expect(saleService.query).toHaveBeenCalled();
      expect(saleService.addSaleToCollectionIfMissing).toHaveBeenCalledWith(
        saleCollection,
        ...additionalSales.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.salesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Product query and add missing value', () => {
      const saleLine: ISaleLine = { id: 10851 };
      const product: IProduct = { id: 21536 };
      saleLine.product = product;

      const productCollection: IProduct[] = [{ id: 21536 }];
      vitest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      vitest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ saleLine });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.productsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const saleLine: ISaleLine = { id: 10851 };
      const sale: ISale = { id: 2908 };
      saleLine.sale = sale;
      const product: IProduct = { id: 21536 };
      saleLine.product = product;

      activatedRoute.data = of({ saleLine });
      comp.ngOnInit();

      expect(comp.salesSharedCollection()).toContainEqual(sale);
      expect(comp.productsSharedCollection()).toContainEqual(product);
      expect(comp.saleLine).toEqual(saleLine);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISaleLine>();
      const saleLine = { id: 17563 };
      vitest.spyOn(saleLineFormService, 'getSaleLine').mockReturnValue(saleLine);
      vitest.spyOn(saleLineService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleLine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(saleLine);
      saveSubject.complete();

      // THEN
      expect(saleLineFormService.getSaleLine).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(saleLineService.update).toHaveBeenCalledWith(expect.objectContaining(saleLine));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISaleLine>();
      const saleLine = { id: 17563 };
      vitest.spyOn(saleLineFormService, 'getSaleLine').mockReturnValue({ id: null });
      vitest.spyOn(saleLineService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleLine: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(saleLine);
      saveSubject.complete();

      // THEN
      expect(saleLineFormService.getSaleLine).toHaveBeenCalled();
      expect(saleLineService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISaleLine>();
      const saleLine = { id: 17563 };
      vitest.spyOn(saleLineService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleLine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(saleLineService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSale', () => {
      it('should forward to saleService', () => {
        const entity = { id: 2908 };
        const entity2 = { id: 10270 };
        vitest.spyOn(saleService, 'compareSale');
        comp.compareSale(entity, entity2);
        expect(saleService.compareSale).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
