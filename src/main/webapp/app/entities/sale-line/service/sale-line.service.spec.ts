import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ISaleLine } from '../sale-line.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sale-line.test-samples';

import { SaleLineService } from './sale-line.service';

const requireRestSample: ISaleLine = {
  ...sampleWithRequiredData,
};

describe('SaleLine Service', () => {
  let service: SaleLineService;
  let httpMock: HttpTestingController;
  let expectedResult: ISaleLine | ISaleLine[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SaleLineService);
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

    it('should create a SaleLine', () => {
      const saleLine = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(saleLine).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SaleLine', () => {
      const saleLine = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(saleLine).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SaleLine', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SaleLine', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SaleLine', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addSaleLineToCollectionIfMissing', () => {
      it('should add a SaleLine to an empty array', () => {
        const saleLine: ISaleLine = sampleWithRequiredData;
        expectedResult = service.addSaleLineToCollectionIfMissing([], saleLine);
        expect(expectedResult).toEqual([saleLine]);
      });

      it('should not add a SaleLine to an array that contains it', () => {
        const saleLine: ISaleLine = sampleWithRequiredData;
        const saleLineCollection: ISaleLine[] = [
          {
            ...saleLine,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSaleLineToCollectionIfMissing(saleLineCollection, saleLine);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SaleLine to an array that doesn't contain it", () => {
        const saleLine: ISaleLine = sampleWithRequiredData;
        const saleLineCollection: ISaleLine[] = [sampleWithPartialData];
        expectedResult = service.addSaleLineToCollectionIfMissing(saleLineCollection, saleLine);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(saleLine);
      });

      it('should add only unique SaleLine to an array', () => {
        const saleLineArray: ISaleLine[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const saleLineCollection: ISaleLine[] = [sampleWithRequiredData];
        expectedResult = service.addSaleLineToCollectionIfMissing(saleLineCollection, ...saleLineArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const saleLine: ISaleLine = sampleWithRequiredData;
        const saleLine2: ISaleLine = sampleWithPartialData;
        expectedResult = service.addSaleLineToCollectionIfMissing([], saleLine, saleLine2);
        expect(expectedResult).toEqual([saleLine, saleLine2]);
      });

      it('should accept null and undefined values', () => {
        const saleLine: ISaleLine = sampleWithRequiredData;
        expectedResult = service.addSaleLineToCollectionIfMissing([], null, saleLine, undefined);
        expect(expectedResult).toEqual([saleLine]);
      });

      it('should return initial array if no SaleLine is added', () => {
        const saleLineCollection: ISaleLine[] = [sampleWithRequiredData];
        expectedResult = service.addSaleLineToCollectionIfMissing(saleLineCollection, undefined, null);
        expect(expectedResult).toEqual(saleLineCollection);
      });
    });

    describe('compareSaleLine', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSaleLine(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17563 };
        const entity2 = null;

        const compareResult1 = service.compareSaleLine(entity1, entity2);
        const compareResult2 = service.compareSaleLine(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17563 };
        const entity2 = { id: 10851 };

        const compareResult1 = service.compareSaleLine(entity1, entity2);
        const compareResult2 = service.compareSaleLine(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 17563 };
        const entity2 = { id: 17563 };

        const compareResult1 = service.compareSaleLine(entity1, entity2);
        const compareResult2 = service.compareSaleLine(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
