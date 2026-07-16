package com.commercial.activities.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.commercial.activities.domain.SaleLine} entity. This class is used
 * in {@link com.commercial.activities.web.rest.SaleLineResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sale-lines?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleLineCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter barcode;

    private IntegerFilter quantity;

    private BigDecimalFilter unitPrice;

    private BigDecimalFilter totalPrice;

    private LongFilter saleId;

    private LongFilter productId;

    private Boolean distinct;

    public SaleLineCriteria() {}

    public SaleLineCriteria(SaleLineCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.barcode = other.optionalBarcode().map(StringFilter::copy).orElse(null);
        this.quantity = other.optionalQuantity().map(IntegerFilter::copy).orElse(null);
        this.unitPrice = other.optionalUnitPrice().map(BigDecimalFilter::copy).orElse(null);
        this.totalPrice = other.optionalTotalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.saleId = other.optionalSaleId().map(LongFilter::copy).orElse(null);
        this.productId = other.optionalProductId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SaleLineCriteria copy() {
        return new SaleLineCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getBarcode() {
        return barcode;
    }

    public Optional<StringFilter> optionalBarcode() {
        return Optional.ofNullable(barcode);
    }

    public StringFilter barcode() {
        if (barcode == null) {
            setBarcode(new StringFilter());
        }
        return barcode;
    }

    public void setBarcode(StringFilter barcode) {
        this.barcode = barcode;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public Optional<IntegerFilter> optionalQuantity() {
        return Optional.ofNullable(quantity);
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            setQuantity(new IntegerFilter());
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public BigDecimalFilter getUnitPrice() {
        return unitPrice;
    }

    public Optional<BigDecimalFilter> optionalUnitPrice() {
        return Optional.ofNullable(unitPrice);
    }

    public BigDecimalFilter unitPrice() {
        if (unitPrice == null) {
            setUnitPrice(new BigDecimalFilter());
        }
        return unitPrice;
    }

    public void setUnitPrice(BigDecimalFilter unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimalFilter getTotalPrice() {
        return totalPrice;
    }

    public Optional<BigDecimalFilter> optionalTotalPrice() {
        return Optional.ofNullable(totalPrice);
    }

    public BigDecimalFilter totalPrice() {
        if (totalPrice == null) {
            setTotalPrice(new BigDecimalFilter());
        }
        return totalPrice;
    }

    public void setTotalPrice(BigDecimalFilter totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LongFilter getSaleId() {
        return saleId;
    }

    public Optional<LongFilter> optionalSaleId() {
        return Optional.ofNullable(saleId);
    }

    public LongFilter saleId() {
        if (saleId == null) {
            setSaleId(new LongFilter());
        }
        return saleId;
    }

    public void setSaleId(LongFilter saleId) {
        this.saleId = saleId;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public Optional<LongFilter> optionalProductId() {
        return Optional.ofNullable(productId);
    }

    public LongFilter productId() {
        if (productId == null) {
            setProductId(new LongFilter());
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SaleLineCriteria that = (SaleLineCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(barcode, that.barcode) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(unitPrice, that.unitPrice) &&
            Objects.equals(totalPrice, that.totalPrice) &&
            Objects.equals(saleId, that.saleId) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, barcode, quantity, unitPrice, totalPrice, saleId, productId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleLineCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBarcode().map(f -> "barcode=" + f + ", ").orElse("") +
            optionalQuantity().map(f -> "quantity=" + f + ", ").orElse("") +
            optionalUnitPrice().map(f -> "unitPrice=" + f + ", ").orElse("") +
            optionalTotalPrice().map(f -> "totalPrice=" + f + ", ").orElse("") +
            optionalSaleId().map(f -> "saleId=" + f + ", ").orElse("") +
            optionalProductId().map(f -> "productId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
