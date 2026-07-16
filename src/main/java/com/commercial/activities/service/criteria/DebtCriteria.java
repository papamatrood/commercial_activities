package com.commercial.activities.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.commercial.activities.domain.Debt} entity. This class is used
 * in {@link com.commercial.activities.web.rest.DebtResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /debts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DebtCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter totalAmount;

    private BigDecimalFilter amountPaid;

    private BigDecimalFilter remainingAmount;

    private InstantFilter date;

    private LongFilter companyId;

    private LongFilter saleId;

    private Boolean distinct;

    public DebtCriteria() {}

    public DebtCriteria(DebtCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.amountPaid = other.optionalAmountPaid().map(BigDecimalFilter::copy).orElse(null);
        this.remainingAmount = other.optionalRemainingAmount().map(BigDecimalFilter::copy).orElse(null);
        this.date = other.optionalDate().map(InstantFilter::copy).orElse(null);
        this.companyId = other.optionalCompanyId().map(LongFilter::copy).orElse(null);
        this.saleId = other.optionalSaleId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DebtCriteria copy() {
        return new DebtCriteria(this);
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

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public Optional<BigDecimalFilter> optionalTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            setTotalAmount(new BigDecimalFilter());
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimalFilter getAmountPaid() {
        return amountPaid;
    }

    public Optional<BigDecimalFilter> optionalAmountPaid() {
        return Optional.ofNullable(amountPaid);
    }

    public BigDecimalFilter amountPaid() {
        if (amountPaid == null) {
            setAmountPaid(new BigDecimalFilter());
        }
        return amountPaid;
    }

    public void setAmountPaid(BigDecimalFilter amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimalFilter getRemainingAmount() {
        return remainingAmount;
    }

    public Optional<BigDecimalFilter> optionalRemainingAmount() {
        return Optional.ofNullable(remainingAmount);
    }

    public BigDecimalFilter remainingAmount() {
        if (remainingAmount == null) {
            setRemainingAmount(new BigDecimalFilter());
        }
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimalFilter remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public InstantFilter getDate() {
        return date;
    }

    public Optional<InstantFilter> optionalDate() {
        return Optional.ofNullable(date);
    }

    public InstantFilter date() {
        if (date == null) {
            setDate(new InstantFilter());
        }
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
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
        final DebtCriteria that = (DebtCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(amountPaid, that.amountPaid) &&
            Objects.equals(remainingAmount, that.remainingAmount) &&
            Objects.equals(date, that.date) &&
            Objects.equals(companyId, that.companyId) &&
            Objects.equals(saleId, that.saleId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, totalAmount, amountPaid, remainingAmount, date, companyId, saleId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DebtCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalAmountPaid().map(f -> "amountPaid=" + f + ", ").orElse("") +
            optionalRemainingAmount().map(f -> "remainingAmount=" + f + ", ").orElse("") +
            optionalDate().map(f -> "date=" + f + ", ").orElse("") +
            optionalCompanyId().map(f -> "companyId=" + f + ", ").orElse("") +
            optionalSaleId().map(f -> "saleId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
