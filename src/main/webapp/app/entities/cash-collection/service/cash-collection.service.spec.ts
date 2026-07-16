import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ICashCollection } from '../cash-collection.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../cash-collection.test-samples';

import { CashCollectionService, RestCashCollection } from './cash-collection.service';

const requireRestSample: RestCashCollection = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('CashCollection Service', () => {
  let service: CashCollectionService;
  let httpMock: HttpTestingController;
  let expectedResult: ICashCollection | ICashCollection[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CashCollectionService);
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

    it('should create a CashCollection', () => {
      const cashCollection = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cashCollection).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CashCollection', () => {
      const cashCollection = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cashCollection).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CashCollection', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CashCollection', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CashCollection', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addCashCollectionToCollectionIfMissing', () => {
      it('should add a CashCollection to an empty array', () => {
        const cashCollection: ICashCollection = sampleWithRequiredData;
        expectedResult = service.addCashCollectionToCollectionIfMissing([], cashCollection);
        expect(expectedResult).toEqual([cashCollection]);
      });

      it('should not add a CashCollection to an array that contains it', () => {
        const cashCollection: ICashCollection = sampleWithRequiredData;
        const cashCollectionCollection: ICashCollection[] = [
          {
            ...cashCollection,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCashCollectionToCollectionIfMissing(cashCollectionCollection, cashCollection);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CashCollection to an array that doesn't contain it", () => {
        const cashCollection: ICashCollection = sampleWithRequiredData;
        const cashCollectionCollection: ICashCollection[] = [sampleWithPartialData];
        expectedResult = service.addCashCollectionToCollectionIfMissing(cashCollectionCollection, cashCollection);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cashCollection);
      });

      it('should add only unique CashCollection to an array', () => {
        const cashCollectionArray: ICashCollection[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cashCollectionCollection: ICashCollection[] = [sampleWithRequiredData];
        expectedResult = service.addCashCollectionToCollectionIfMissing(cashCollectionCollection, ...cashCollectionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cashCollection: ICashCollection = sampleWithRequiredData;
        const cashCollection2: ICashCollection = sampleWithPartialData;
        expectedResult = service.addCashCollectionToCollectionIfMissing([], cashCollection, cashCollection2);
        expect(expectedResult).toEqual([cashCollection, cashCollection2]);
      });

      it('should accept null and undefined values', () => {
        const cashCollection: ICashCollection = sampleWithRequiredData;
        expectedResult = service.addCashCollectionToCollectionIfMissing([], null, cashCollection, undefined);
        expect(expectedResult).toEqual([cashCollection]);
      });

      it('should return initial array if no CashCollection is added', () => {
        const cashCollectionCollection: ICashCollection[] = [sampleWithRequiredData];
        expectedResult = service.addCashCollectionToCollectionIfMissing(cashCollectionCollection, undefined, null);
        expect(expectedResult).toEqual(cashCollectionCollection);
      });
    });

    describe('compareCashCollection', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCashCollection(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 8031 };
        const entity2 = null;

        const compareResult1 = service.compareCashCollection(entity1, entity2);
        const compareResult2 = service.compareCashCollection(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 8031 };
        const entity2 = { id: 5994 };

        const compareResult1 = service.compareCashCollection(entity1, entity2);
        const compareResult2 = service.compareCashCollection(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 8031 };
        const entity2 = { id: 8031 };

        const compareResult1 = service.compareCashCollection(entity1, entity2);
        const compareResult2 = service.compareCashCollection(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
