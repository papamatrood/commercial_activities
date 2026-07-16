package com.commercial.activities.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.commercial.activities.domain.Product} entity. This class is used
 * in {@link com.commercial.activities.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter barcode;

    private StringFilter designation;

    private IntegerFilter initialStock;

    private IntegerFilter currentStock;

    private BigDecimalFilter unitPrice;

    private LongFilter companyId;

    private LongFilter supplierId;

    private Boolean distinct;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.barcode = other.optionalBarcode().map(StringFilter::copy).orElse(null);
        this.designation = other.optionalDesignation().map(StringFilter::copy).orElse(null);
        this.initialStock = other.optionalInitialStock().map(IntegerFilter::copy).orElse(null);
        this.currentStock = other.optionalCurrentStock().map(IntegerFilter::copy).orElse(null);
        this.unitPrice = other.optionalUnitPrice().map(BigDecimalFilter::copy).orElse(null);
        this.companyId = other.optionalCompanyId().map(LongFilter::copy).orElse(null);
        this.supplierId = other.optionalSupplierId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
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

    public StringFilter getDesignation() {
        return designation;
    }

    public Optional<StringFilter> optionalDesignation() {
        return Optional.ofNullable(designation);
    }

    public StringFilter designation() {
        if (designation == null) {
            setDesignation(new StringFilter());
        }
        return designation;
    }

    public void setDesignation(StringFilter designation) {
        this.designation = designation;
    }

    public IntegerFilter getInitialStock() {
        return initialStock;
    }

    public Optional<IntegerFilter> optionalInitialStock() {
        return Optional.ofNullable(initialStock);
    }

    public IntegerFilter initialStock() {
        if (initialStock == null) {
            setInitialStock(new IntegerFilter());
        }
        return initialStock;
    }

    public void setInitialStock(IntegerFilter initialStock) {
        this.initialStock = initialStock;
    }

    public IntegerFilter getCurrentStock() {
        return currentStock;
    }

    public Optional<IntegerFilter> optionalCurrentStock() {
        return Optional.ofNullable(currentStock);
    }

    public IntegerFilter currentStock() {
        if (currentStock == null) {
            setCurrentStock(new IntegerFilter());
        }
        return currentStock;
    }

    public void setCurrentStock(IntegerFilter currentStock) {
        this.currentStock = currentStock;
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

    public LongFilter getCompanyId() {
        return companyId;
    }

    public Optional<LongFilter> optionalCompanyId() {
        return Optional.ofNullable(companyId);
    }

    public LongFilter companyId() {
        if (companyId == null) {
            setCompanyId(new LongFilter());
        }
        return companyId;
    }

    public void setCompanyId(LongFilter companyId) {
        this.companyId = companyId;
    }

    public LongFilter getSupplierId() {
        return supplierId;
    }

    public Optional<LongFilter> optionalSupplierId() {
        return Optional.ofNullable(supplierId);
    }

    public LongFilter supplierId() {
        if (supplierId == null) {
            setSupplierId(new LongFilter());
        }
        return supplierId;
    }

    public void setSupplierId(LongFilter supplierId) {
        this.supplierId = supplierId;
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
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(barcode, that.barcode) &&
            Objects.equals(designation, that.designation) &&
            Objects.equals(initialStock, that.initialStock) &&
            Objects.equals(currentStock, that.currentStock) &&
            Objects.equals(unitPrice, that.unitPrice) &&
            Objects.equals(companyId, that.companyId) &&
            Objects.equals(supplierId, that.supplierId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, barcode, designation, initialStock, currentStock, unitPrice, companyId, supplierId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBarcode().map(f -> "barcode=" + f + ", ").orElse("") +
            optionalDesignation().map(f -> "designation=" + f + ", ").orElse("") +
            optionalInitialStock().map(f -> "initialStock=" + f + ", ").orElse("") +
            optionalCurrentStock().map(f -> "currentStock=" + f + ", ").orElse("") +
            optionalUnitPrice().map(f -> "unitPrice=" + f + ", ").orElse("") +
            optionalCompanyId().map(f -> "companyId=" + f + ", ").orElse("") +
            optionalSupplierId().map(f -> "supplierId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
