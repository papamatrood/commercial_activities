import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ICashDisbursement } from '../cash-disbursement.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../cash-disbursement.test-samples';

import { CashDisbursementService, RestCashDisbursement } from './cash-disbursement.service';

const requireRestSample: RestCashDisbursement = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('CashDisbursement Service', () => {
  let service: CashDisbursementService;
  let httpMock: HttpTestingController;
  let expectedResult: ICashDisbursement | ICashDisbursement[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CashDisbursementService);
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

    it('should create a CashDisbursement', () => {
      const cashDisbursement = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cashDisbursement).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CashDisbursement', () => {
      const cashDisbursement = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cashDisbursement).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CashDisbursement', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CashDisbursement', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CashDisbursement', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addCashDisbursementToCollectionIfMissing', () => {
      it('should add a CashDisbursement to an empty array', () => {
        const cashDisbursement: ICashDisbursement = sampleWithRequiredData;
        expectedResult = service.addCashDisbursementToCollectionIfMissing([], cashDisbursement);
        expect(expectedResult).toEqual([cashDisbursement]);
      });

      it('should not add a CashDisbursement to an array that contains it', () => {
        const cashDisbursement: ICashDisbursement = sampleWithRequiredData;
        const cashDisbursementCollection: ICashDisbursement[] = [
          {
            ...cashDisbursement,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCashDisbursementToCollectionIfMissing(cashDisbursementCollection, cashDisbursement);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CashDisbursement to an array that doesn't contain it", () => {
        const cashDisbursement: ICashDisbursement = sampleWithRequiredData;
        const cashDisbursementCollection: ICashDisbursement[] = [sampleWithPartialData];
        expectedResult = service.addCashDisbursementToCollectionIfMissing(cashDisbursementCollection, cashDisbursement);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cashDisbursement);
      });

      it('should add only unique CashDisbursement to an array', () => {
        const cashDisbursementArray: ICashDisbursement[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cashDisbursementCollection: ICashDisbursement[] = [sampleWithRequiredData];
        expectedResult = service.addCashDisbursementToCollectionIfMissing(cashDisbursementCollection, ...cashDisbursementArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cashDisbursement: ICashDisbursement = sampleWithRequiredData;
        const cashDisbursement2: ICashDisbursement = sampleWithPartialData;
        expectedResult = service.addCashDisbursementToCollectionIfMissing([], cashDisbursement, cashDisbursement2);
        expect(expectedResult).toEqual([cashDisbursement, cashDisbursement2]);
      });

      it('should accept null and undefined values', () => {
        const cashDisbursement: ICashDisbursement = sampleWithRequiredData;
        expectedResult = service.addCashDisbursementToCollectionIfMissing([], null, cashDisbursement, undefined);
        expect(expectedResult).toEqual([cashDisbursement]);
      });

      it('should return initial array if no CashDisbursement is added', () => {
        const cashDisbursementCollection: ICashDisbursement[] = [sampleWithRequiredData];
        expectedResult = service.addCashDisbursementToCollectionIfMissing(cashDisbursementCollection, undefined, null);
        expect(expectedResult).toEqual(cashDisbursementCollection);
      });
    });

    describe('compareCashDisbursement', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCashDisbursement(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 16878 };
        const entity2 = null;

        const compareResult1 = service.compareCashDisbursement(entity1, entity2);
        const compareResult2 = service.compareCashDisbursement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 16878 };
        const entity2 = { id: 2526 };

        const compareResult1 = service.compareCashDisbursement(entity1, entity2);
        const compareResult2 = service.compareCashDisbursement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 16878 };
        const entity2 = { id: 16878 };

        const compareResult1 = service.compareCashDisbursement(entity1, entity2);
        const compareResult2 = service.compareCashDisbursement(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
