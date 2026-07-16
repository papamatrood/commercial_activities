import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IDebt } from '../debt.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../debt.test-samples';

import { DebtService, RestDebt } from './debt.service';

const requireRestSample: RestDebt = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('Debt Service', () => {
  let service: DebtService;
  let httpMock: HttpTestingController;
  let expectedResult: IDebt | IDebt[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DebtService);
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

    it('should create a Debt', () => {
      const debt = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(debt).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Debt', () => {
      const debt = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(debt).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Debt', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Debt', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Debt', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addDebtToCollectionIfMissing', () => {
      it('should add a Debt to an empty array', () => {
        const debt: IDebt = sampleWithRequiredData;
        expectedResult = service.addDebtToCollectionIfMissing([], debt);
        expect(expectedResult).toEqual([debt]);
      });

      it('should not add a Debt to an array that contains it', () => {
        const debt: IDebt = sampleWithRequiredData;
        const debtCollection: IDebt[] = [
          {
            ...debt,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDebtToCollectionIfMissing(debtCollection, debt);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Debt to an array that doesn't contain it", () => {
        const debt: IDebt = sampleWithRequiredData;
        const debtCollection: IDebt[] = [sampleWithPartialData];
        expectedResult = service.addDebtToCollectionIfMissing(debtCollection, debt);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(debt);
      });

      it('should add only unique Debt to an array', () => {
        const debtArray: IDebt[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const debtCollection: IDebt[] = [sampleWithRequiredData];
        expectedResult = service.addDebtToCollectionIfMissing(debtCollection, ...debtArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const debt: IDebt = sampleWithRequiredData;
        const debt2: IDebt = sampleWithPartialData;
        expectedResult = service.addDebtToCollectionIfMissing([], debt, debt2);
        expect(expectedResult).toEqual([debt, debt2]);
      });

      it('should accept null and undefined values', () => {
        const debt: IDebt = sampleWithRequiredData;
        expectedResult = service.addDebtToCollectionIfMissing([], null, debt, undefined);
        expect(expectedResult).toEqual([debt]);
      });

      it('should return initial array if no Debt is added', () => {
        const debtCollection: IDebt[] = [sampleWithRequiredData];
        expectedResult = service.addDebtToCollectionIfMissing(debtCollection, undefined, null);
        expect(expectedResult).toEqual(debtCollection);
      });
    });

    describe('compareDebt', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDebt(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 21142 };
        const entity2 = null;

        const compareResult1 = service.compareDebt(entity1, entity2);
        const compareResult2 = service.compareDebt(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 21142 };
        const entity2 = { id: 12844 };

        const compareResult1 = service.compareDebt(entity1, entity2);
        const compareResult2 = service.compareDebt(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 21142 };
        const entity2 = { id: 21142 };

        const compareResult1 = service.compareDebt(entity1, entity2);
        const compareResult2 = service.compareDebt(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
