import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ISaleLine } from 'app/entities/sale-line/sale-line.model';
import { SaleLineService } from 'app/entities/sale-line/service/sale-line.service';

import { SaleLineFormGroup, SaleLineFormService } from './sale-line-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-sale-line-update',
  templateUrl: './sale-line-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SaleLineUpdate implements OnInit {
  readonly isSaving = signal(false);
  saleLine: ISaleLine | null = null;

  salesSharedCollection = signal<ISale[]>([]);
  productsSharedCollection = signal<IProduct[]>([]);

  protected saleLineService = inject(SaleLineService);
  protected saleLineFormService = inject(SaleLineFormService);
  protected saleService = inject(SaleService);
  protected productService = inject(ProductService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SaleLineFormGroup = this.saleLineFormService.createSaleLineFormGroup();

  compareSale = (o1: ISale | null, o2: ISale | null): boolean => this.saleService.compareSale(o1, o2);

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ saleLine }) => {
      this.saleLine = saleLine;
      if (saleLine) {
        this.updateForm(saleLine);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const saleLine = this.saleLineFormService.getSaleLine(this.editForm);
    if (saleLine.id === null) {
      this.subscribeToSaveResponse(this.saleLineService.create(saleLine));
    } else {
      this.subscribeToSaveResponse(this.saleLineService.update(saleLine));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISaleLine | null>): void {
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

  protected updateForm(saleLine: ISaleLine): void {
    this.saleLine = saleLine;
    this.saleLineFormService.resetForm(this.editForm, saleLine);

    this.salesSharedCollection.update(sales => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, saleLine.sale));
    this.productsSharedCollection.update(products =>
      this.productService.addProductToCollectionIfMissing<IProduct>(products, saleLine.product),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.saleService
      .query()
      .pipe(map((res: HttpResponse<ISale[]>) => res.body ?? []))
      .pipe(map((sales: ISale[]) => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, this.saleLine?.sale)))
      .subscribe((sales: ISale[]) => this.salesSharedCollection.set(sales));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing<IProduct>(products, this.saleLine?.product)))
      .subscribe((products: IProduct[]) => this.productsSharedCollection.set(products));
  }
}
