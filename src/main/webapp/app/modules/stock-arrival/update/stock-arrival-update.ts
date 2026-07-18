import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { StockArrivalService } from 'app/entities/stock-arrival/service/stock-arrival.service';
import { IStockArrival } from 'app/entities/stock-arrival/stock-arrival.model';

import { StockArrivalFormGroup, StockArrivalFormService } from './stock-arrival-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-stock-arrival-update',
  templateUrl: './stock-arrival-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class StockArrivalUpdate implements OnInit {
  readonly isSaving = signal(false);
  stockArrival: IStockArrival | null = null;

  productsSharedCollection = signal<IProduct[]>([]);

  protected stockArrivalService = inject(StockArrivalService);
  protected stockArrivalFormService = inject(StockArrivalFormService);
  protected productService = inject(ProductService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StockArrivalFormGroup = this.stockArrivalFormService.createStockArrivalFormGroup();

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stockArrival }) => {
      this.stockArrival = stockArrival;
      if (stockArrival) {
        this.updateForm(stockArrival);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const stockArrival = this.stockArrivalFormService.getStockArrival(this.editForm);
    if (stockArrival.id === null) {
      this.subscribeToSaveResponse(this.stockArrivalService.create(stockArrival));
    } else {
      this.subscribeToSaveResponse(this.stockArrivalService.update(stockArrival));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IStockArrival | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(stockArrival: IStockArrival): void {
    this.stockArrival = stockArrival;
    this.stockArrivalFormService.resetForm(this.editForm, stockArrival);

    this.productsSharedCollection.update(products =>
      this.productService.addProductToCollectionIfMissing<IProduct>(products, stockArrival.product),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing<IProduct>(products, this.stockArrival?.product)),
      )
      .subscribe((products: IProduct[]) => this.productsSharedCollection.set(products));
  }
}
