import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IDebtPayment } from '../debt-payment.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../debt-payment.test-samples';

import { DebtPaymentService, RestDebtPayment } from './debt-payment.service';

const requireRestSample: RestDebtPayment = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('DebtPayment Service', () => {
  let service: DebtPaymentService;
  let httpMock: HttpTestingController;
  let expectedResult: IDebtPayment | IDebtPayment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DebtPaymentService);
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

    it('should create a DebtPayment', () => {
      const debtPayment = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(debtPayment).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DebtPayment', () => {
      const debtPayment = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(debtPayment).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DebtPayment', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DebtPayment', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DebtPayment', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addDebtPaymentToCollectionIfMissing', () => {
      it('should add a DebtPayment to an empty array', () => {
        const debtPayment: IDebtPayment = sampleWithRequiredData;
        expectedResult = service.addDebtPaymentToCollectionIfMissing([], debtPayment);
        expect(expectedResult).toEqual([debtPayment]);
      });

      it('should not add a DebtPayment to an array that contains it', () => {
        const debtPayment: IDebtPayment = sampleWithRequiredData;
        const debtPaymentCollection: IDebtPayment[] = [
          {
            ...debtPayment,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDebtPaymentToCollectionIfMissing(debtPaymentCollection, debtPayment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DebtPayment to an array that doesn't contain it", () => {
        const debtPayment: IDebtPayment = sampleWithRequiredData;
        const debtPaymentCollection: IDebtPayment[] = [sampleWithPartialData];
        expectedResult = service.addDebtPaymentToCollectionIfMissing(debtPaymentCollection, debtPayment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(debtPayment);
      });

      it('should add only unique DebtPayment to an array', () => {
        const debtPaymentArray: IDebtPayment[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const debtPaymentCollection: IDebtPayment[] = [sampleWithRequiredData];
        expectedResult = service.addDebtPaymentToCollectionIfMissing(debtPaymentCollection, ...debtPaymentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const debtPayment: IDebtPayment = sampleWithRequiredData;
        const debtPayment2: IDebtPayment = sampleWithPartialData;
        expectedResult = service.addDebtPaymentToCollectionIfMissing([], debtPayment, debtPayment2);
        expect(expectedResult).toEqual([debtPayment, debtPayment2]);
      });

      it('should accept null and undefined values', () => {
        const debtPayment: IDebtPayment = sampleWithRequiredData;
        expectedResult = service.addDebtPaymentToCollectionIfMissing([], null, debtPayment, undefined);
        expect(expectedResult).toEqual([debtPayment]);
      });

      it('should return initial array if no DebtPayment is added', () => {
        const debtPaymentCollection: IDebtPayment[] = [sampleWithRequiredData];
        expectedResult = service.addDebtPaymentToCollectionIfMissing(debtPaymentCollection, undefined, null);
        expect(expectedResult).toEqual(debtPaymentCollection);
      });
    });

    describe('compareDebtPayment', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDebtPayment(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 942 };
        const entity2 = null;

        const compareResult1 = service.compareDebtPayment(entity1, entity2);
        const compareResult2 = service.compareDebtPayment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 942 };
        const entity2 = { id: 1541 };

        const compareResult1 = service.compareDebtPayment(entity1, entity2);
        const compareResult2 = service.compareDebtPayment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 942 };
        const entity2 = { id: 942 };

        const compareResult1 = service.compareDebtPayment(entity1, entity2);
        const compareResult2 = service.compareDebtPayment(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
