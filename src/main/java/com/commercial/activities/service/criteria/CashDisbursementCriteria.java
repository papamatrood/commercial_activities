package com.commercial.activities.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.commercial.activities.domain.CashDisbursement} entity. This class is used
 * in {@link com.commercial.activities.web.rest.CashDisbursementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cash-disbursements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CashDisbursementCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reason;

    private BigDecimalFilter amount;

    private InstantFilter date;

    private LongFilter companyId;

    private LongFilter userId;

    private Boolean distinct;

    public CashDisbursementCriteria() {}

    public CashDisbursementCriteria(CashDisbursementCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reason = other.optionalReason().map(StringFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.date = other.optionalDate().map(InstantFilter::copy).orElse(null);
        this.companyId = other.optionalCompanyId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CashDisbursementCriteria copy() {
        return new CashDisbursementCriteria(this);
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

    public StringFilter getReason() {
        return reason;
    }

    public Optional<StringFilter> optionalReason() {
        return Optional.ofNullable(reason);
    }

    public StringFilter reason() {
        if (reason == null) {
            setReason(new StringFilter());
        }
        return reason;
    }

    public void setReason(StringFilter reason) {
        this.reason = reason;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public Optional<BigDecimalFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            setAmount(new BigDecimalFilter());
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
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

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final CashDisbursementCriteria that = (CashDisbursementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reason, that.reason) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(date, that.date) &&
            Objects.equals(companyId, that.companyId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reason, amount, date, companyId, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CashDisbursementCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReason().map(f -> "reason=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalDate().map(f -> "date=" + f + ", ").orElse("") +
            optionalCompanyId().map(f -> "companyId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
