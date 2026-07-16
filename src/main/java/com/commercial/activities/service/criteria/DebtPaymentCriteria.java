package com.commercial.activities.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.commercial.activities.domain.DebtPayment} entity. This class is used
 * in {@link com.commercial.activities.web.rest.DebtPaymentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /debt-payments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DebtPaymentCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter amountPaid;

    private BigDecimalFilter remainingAmount;

    private InstantFilter date;

    private LongFilter debtId;

    private Boolean distinct;

    public DebtPaymentCriteria() {}

    public DebtPaymentCriteria(DebtPaymentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.amountPaid = other.optionalAmountPaid().map(BigDecimalFilter::copy).orElse(null);
        this.remainingAmount = other.optionalRemainingAmount().map(BigDecimalFilter::copy).orElse(null);
        this.date = other.optionalDate().map(InstantFilter::copy).orElse(null);
        this.debtId = other.optionalDebtId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DebtPaymentCriteria copy() {
        return new DebtPaymentCriteria(this);
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

    public LongFilter getDebtId() {
        return debtId;
    }

    public Optional<LongFilter> optionalDebtId() {
        return Optional.ofNullable(debtId);
    }

    public LongFilter debtId() {
        if (debtId == null) {
            setDebtId(new LongFilter());
        }
        return debtId;
    }

    public void setDebtId(LongFilter debtId) {
        this.debtId = debtId;
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
        final DebtPaymentCriteria that = (DebtPaymentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(amountPaid, that.amountPaid) &&
            Objects.equals(remainingAmount, that.remainingAmount) &&
            Objects.equals(date, that.date) &&
            Objects.equals(debtId, that.debtId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amountPaid, remainingAmount, date, debtId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DebtPaymentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAmountPaid().map(f -> "amountPaid=" + f + ", ").orElse("") +
            optionalRemainingAmount().map(f -> "remainingAmount=" + f + ", ").orElse("") +
            optionalDate().map(f -> "date=" + f + ", ").orElse("") +
            optionalDebtId().map(f -> "debtId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
