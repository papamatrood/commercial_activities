package com.commercial.activities.service.criteria;

import com.commercial.activities.domain.enumeration.CompanyStatusEnum;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.commercial.activities.domain.Company} entity. This class is used
 * in {@link com.commercial.activities.web.rest.CompanyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /companies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompanyCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CompanyStatusEnum
     */
    public static class CompanyStatusEnumFilter extends Filter<CompanyStatusEnum> {

        public CompanyStatusEnumFilter() {}

        public CompanyStatusEnumFilter(CompanyStatusEnumFilter filter) {
            super(filter);
        }

        @Override
        public CompanyStatusEnumFilter copy() {
            return new CompanyStatusEnumFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter location;

    private InstantFilter creationDate;

    private CompanyStatusEnumFilter status;

    private Boolean distinct;

    public CompanyCriteria() {}

    public CompanyCriteria(CompanyCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.location = other.optionalLocation().map(StringFilter::copy).orElse(null);
        this.creationDate = other.optionalCreationDate().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(CompanyStatusEnumFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CompanyCriteria copy() {
        return new CompanyCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getLocation() {
        return location;
    }

    public Optional<StringFilter> optionalLocation() {
        return Optional.ofNullable(location);
    }

    public StringFilter location() {
        if (location == null) {
            setLocation(new StringFilter());
        }
        return location;
    }

    public void setLocation(StringFilter location) {
        this.location = location;
    }

    public InstantFilter getCreationDate() {
        return creationDate;
    }

    public Optional<InstantFilter> optionalCreationDate() {
        return Optional.ofNullable(creationDate);
    }

    public InstantFilter creationDate() {
        if (creationDate == null) {
            setCreationDate(new InstantFilter());
        }
        return creationDate;
    }

    public void setCreationDate(InstantFilter creationDate) {
        this.creationDate = creationDate;
    }

    public CompanyStatusEnumFilter getStatus() {
        return status;
    }

    public Optional<CompanyStatusEnumFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public CompanyStatusEnumFilter status() {
        if (status == null) {
            setStatus(new CompanyStatusEnumFilter());
        }
        return status;
    }

    public void setStatus(CompanyStatusEnumFilter status) {
        this.status = status;
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
        final CompanyCriteria that = (CompanyCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(location, that.location) &&
            Objects.equals(creationDate, that.creationDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, creationDate, status, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompanyCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalLocation().map(f -> "location=" + f + ", ").orElse("") +
            optionalCreationDate().map(f -> "creationDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
