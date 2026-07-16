import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { provideTranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { CashDisbursementDetail } from './cash-disbursement-detail';

describe('CashDisbursement Management Detail Component', () => {
  let comp: CashDisbursementDetail;
  let fixture: ComponentFixture<CashDisbursementDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./cash-disbursement-detail').then(m => m.CashDisbursementDetail),
              resolve: { cashDisbursement: () => of({ id: 16878 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CashDisbursementDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load cashDisbursement on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CashDisbursementDetail);

      // THEN
      expect(instance.cashDisbursement()).toEqual(expect.objectContaining({ id: 16878 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vitest.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
