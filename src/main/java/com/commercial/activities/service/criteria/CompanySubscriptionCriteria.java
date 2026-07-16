package com.commercial.activities.service.criteria;

import com.commercial.activities.domain.enumeration.CompanySubscriptionStatusEnum;
import com.commercial.activities.domain.enumeration.CompanySubscriptionTypeEnum;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.commercial.activities.domain.CompanySubscription} entity. This class is used
 * in {@link com.commercial.activities.web.rest.CompanySubscriptionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /company-subscriptions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompanySubscriptionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CompanySubscriptionTypeEnum
     */
    public static class CompanySubscriptionTypeEnumFilter extends Filter<CompanySubscriptionTypeEnum> {

        public CompanySubscriptionTypeEnumFilter() {}

        public CompanySubscriptionTypeEnumFilter(CompanySubscriptionTypeEnumFilter filter) {
            super(filter);
        }

        @Override
        public CompanySubscriptionTypeEnumFilter copy() {
            return new CompanySubscriptionTypeEnumFilter(this);
        }
    }

    /**
     * Class for filtering CompanySubscriptionStatusEnum
     */
    public static class CompanySubscriptionStatusEnumFilter extends Filter<CompanySubscriptionStatusEnum> {

        public CompanySubscriptionStatusEnumFilter() {}

        public CompanySubscriptionStatusEnumFilter(CompanySubscriptionStatusEnumFilter filter) {
            super(filter);
        }

        @Override
        public CompanySubscriptionStatusEnumFilter copy() {
            return new CompanySubscriptionStatusEnumFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private CompanySubscriptionTypeEnumFilter type;

    private InstantFilter startDate;

    private InstantFilter endDate;

    private CompanySubscriptionStatusEnumFilter status;

    private LongFilter companyId;

    private Boolean distinct;

    public CompanySubscriptionCriteria() {}

    public CompanySubscriptionCriteria(CompanySubscriptionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.type = other.optionalType().map(CompanySubscriptionTypeEnumFilter::copy).orElse(null);
        this.startDate = other.optionalStartDate().map(InstantFilter::copy).orElse(null);
        this.endDate = other.optionalEndDate().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(CompanySubscriptionStatusEnumFilter::copy).orElse(null);
        this.companyId = other.optionalCompanyId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CompanySubscriptionCriteria copy() {
        return new CompanySubscriptionCriteria(this);
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

    public CompanySubscriptionTypeEnumFilter getType() {
        return type;
    }

    public Optional<CompanySubscriptionTypeEnumFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public CompanySubscriptionTypeEnumFilter type() {
        if (type == null) {
            setType(new CompanySubscriptionTypeEnumFilter());
        }
        return type;
    }

    public void setType(CompanySubscriptionTypeEnumFilter type) {
        this.type = type;
    }

    public InstantFilter getStartDate() {
        return startDate;
    }

    public Optional<InstantFilter> optionalStartDate() {
        return Optional.ofNullable(startDate);
    }

    public InstantFilter startDate() {
        if (startDate == null) {
            setStartDate(new InstantFilter());
        }
        return startDate;
    }

    public void setStartDate(InstantFilter startDate) {
        this.startDate = startDate;
    }

    public InstantFilter getEndDate() {
        return endDate;
    }

    public Optional<InstantFilter> optionalEndDate() {
        return Optional.ofNullable(endDate);
    }

    public InstantFilter endDate() {
        if (endDate == null) {
            setEndDate(new InstantFilter());
        }
        return endDate;
    }

    public void setEndDate(InstantFilter endDate) {
        this.endDate = endDate;
    }

    public CompanySubscriptionStatusEnumFilter getStatus() {
        return status;
    }

    public Optional<CompanySubscriptionStatusEnumFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public CompanySubscriptionStatusEnumFilter status() {
        if (status == null) {
            setStatus(new CompanySubscriptionStatusEnumFilter());
        }
        return status;
    }

    public void setStatus(CompanySubscriptionStatusEnumFilter status) {
        this.status = status;
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
        final CompanySubscriptionCriteria that = (CompanySubscriptionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(companyId, that.companyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, startDate, endDate, status, companyId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompanySubscriptionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalStartDate().map(f -> "startDate=" + f + ", ").orElse("") +
            optionalEndDate().map(f -> "endDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCompanyId().map(f -> "companyId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
