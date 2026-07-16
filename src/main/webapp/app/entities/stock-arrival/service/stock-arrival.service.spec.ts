import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IStockArrival } from '../stock-arrival.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../stock-arrival.test-samples';

import { RestStockArrival, StockArrivalService } from './stock-arrival.service';

const requireRestSample: RestStockArrival = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('StockArrival Service', () => {
  let service: StockArrivalService;
  let httpMock: HttpTestingController;
  let expectedResult: IStockArrival | IStockArrival[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(StockArrivalService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a StockArrival', () => {
      const stockArrival = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(stockArrival).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StockArrival', () => {
      const stockArrival = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(stockArrival).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StockArrival', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StockArrival', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a StockArrival', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addStockArrivalToCollectionIfMissing', () => {
      it('should add a StockArrival to an empty array', () => {
        const stockArrival: IStockArrival = sampleWithRequiredData;
        expectedResult = service.addStockArrivalToCollectionIfMissing([], stockArrival);
        expect(expectedResult).toEqual([stockArrival]);
      });

      it('should not add a StockArrival to an array that contains it', () => {
        const stockArrival: IStockArrival = sampleWithRequiredData;
        const stockArrivalCollection: IStockArrival[] = [
          {
            ...stockArrival,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStockArrivalToCollectionIfMissing(stockArrivalCollection, stockArrival);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StockArrival to an array that doesn't contain it", () => {
        const stockArrival: IStockArrival = sampleWithRequiredData;
        const stockArrivalCollection: IStockArrival[] = [sampleWithPartialData];
        expectedResult = service.addStockArrivalToCollectionIfMissing(stockArrivalCollection, stockArrival);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stockArrival);
      });

      it('should add only unique StockArrival to an array', () => {
        const stockArrivalArray: IStockArrival[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const stockArrivalCollection: IStockArrival[] = [sampleWithRequiredData];
        expectedResult = service.addStockArrivalToCollectionIfMissing(stockArrivalCollection, ...stockArrivalArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const stockArrival: IStockArrival = sampleWithRequiredData;
        const stockArrival2: IStockArrival = sampleWithPartialData;
        expectedResult = service.addStockArrivalToCollectionIfMissing([], stockArrival, stockArrival2);
        expect(expectedResult).toEqual([stockArrival, stockArrival2]);
      });

      it('should accept null and undefined values', () => {
        const stockArrival: IStockArrival = sampleWithRequiredData;
        expectedResult = service.addStockArrivalToCollectionIfMissing([], null, stockArrival, undefined);
        expect(expectedResult).toEqual([stockArrival]);
      });

      it('should return initial array if no StockArrival is added', () => {
        const stockArrivalCollection: IStockArrival[] = [sampleWithRequiredData];
        expectedResult = service.addStockArrivalToCollectionIfMissing(stockArrivalCollection, undefined, null);
        expect(expectedResult).toEqual(stockArrivalCollection);
      });
    });

    describe('compareStockArrival', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStockArrival(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 5003 };
        const entity2 = null;

        const compareResult1 = service.compareStockArrival(entity1, entity2);
        const compareResult2 = service.compareStockArrival(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 5003 };
        const entity2 = { id: 6242 };

        const compareResult1 = service.compareStockArrival(entity1, entity2);
        const compareResult2 = service.compareStockArrival(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 5003 };
        const entity2 = { id: 5003 };

        const compareResult1 = service.compareStockArrival(entity1, entity2);
        const compareResult2 = service.compareStockArrival(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
