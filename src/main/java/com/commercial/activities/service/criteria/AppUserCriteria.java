package com.commercial.activities.service.criteria;

import com.commercial.activities.domain.enumeration.AppUserTypeEnum;
import com.commercial.activities.domain.enumeration.GenderEnum;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.commercial.activities.domain.AppUser} entity. This class is used
 * in {@link com.commercial.activities.web.rest.AppUserResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /app-users?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppUserCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AppUserTypeEnum
     */
    public static class AppUserTypeEnumFilter extends Filter<AppUserTypeEnum> {

        public AppUserTypeEnumFilter() {}

        public AppUserTypeEnumFilter(AppUserTypeEnumFilter filter) {
            super(filter);
        }

        @Override
        public AppUserTypeEnumFilter copy() {
            return new AppUserTypeEnumFilter(this);
        }
    }

    /**
     * Class for filtering GenderEnum
     */
    public static class GenderEnumFilter extends Filter<GenderEnum> {

        public GenderEnumFilter() {}

        public GenderEnumFilter(GenderEnumFilter filter) {
            super(filter);
        }

        @Override
        public GenderEnumFilter copy() {
            return new GenderEnumFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter phoneNumber;

    private AppUserTypeEnumFilter type;

    private InstantFilter birthDate;

    private StringFilter birthPlace;

    private GenderEnumFilter gender;

    private BooleanFilter disabled;

    private InstantFilter disabledDate;

    private LongFilter userId;

    private LongFilter companyId;

    private LongFilter permissionId;

    private Boolean distinct;

    public AppUserCriteria() {}

    public AppUserCriteria(AppUserCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.type = other.optionalType().map(AppUserTypeEnumFilter::copy).orElse(null);
        this.birthDate = other.optionalBirthDate().map(InstantFilter::copy).orElse(null);
        this.birthPlace = other.optionalBirthPlace().map(StringFilter::copy).orElse(null);
        this.gender = other.optionalGender().map(GenderEnumFilter::copy).orElse(null);
        this.disabled = other.optionalDisabled().map(BooleanFilter::copy).orElse(null);
        this.disabledDate = other.optionalDisabledDate().map(InstantFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.companyId = other.optionalCompanyId().map(LongFilter::copy).orElse(null);
        this.permissionId = other.optionalPermissionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AppUserCriteria copy() {
        return new AppUserCriteria(this);
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

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public Optional<StringFilter> optionalPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            setPhoneNumber(new StringFilter());
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AppUserTypeEnumFilter getType() {
        return type;
    }

    public Optional<AppUserTypeEnumFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public AppUserTypeEnumFilter type() {
        if (type == null) {
            setType(new AppUserTypeEnumFilter());
        }
        return type;
    }

    public void setType(AppUserTypeEnumFilter type) {
        this.type = type;
    }

    public InstantFilter getBirthDate() {
        return birthDate;
    }

    public Optional<InstantFilter> optionalBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public InstantFilter birthDate() {
        if (birthDate == null) {
            setBirthDate(new InstantFilter());
        }
        return birthDate;
    }

    public void setBirthDate(InstantFilter birthDate) {
        this.birthDate = birthDate;
    }

    public StringFilter getBirthPlace() {
        return birthPlace;
    }

    public Optional<StringFilter> optionalBirthPlace() {
        return Optional.ofNullable(birthPlace);
    }

    public StringFilter birthPlace() {
        if (birthPlace == null) {
            setBirthPlace(new StringFilter());
        }
        return birthPlace;
    }

    public void setBirthPlace(StringFilter birthPlace) {
        this.birthPlace = birthPlace;
    }

    public GenderEnumFilter getGender() {
        return gender;
    }

    public Optional<GenderEnumFilter> optionalGender() {
        return Optional.ofNullable(gender);
    }

    public GenderEnumFilter gender() {
        if (gender == null) {
            setGender(new GenderEnumFilter());
        }
        return gender;
    }

    public void setGender(GenderEnumFilter gender) {
        this.gender = gender;
    }

    public BooleanFilter getDisabled() {
        return disabled;
    }

    public Optional<BooleanFilter> optionalDisabled() {
        return Optional.ofNullable(disabled);
    }

    public BooleanFilter disabled() {
        if (disabled == null) {
            setDisabled(new BooleanFilter());
        }
        return disabled;
    }

    public void setDisabled(BooleanFilter disabled) {
        this.disabled = disabled;
    }

    public InstantFilter getDisabledDate() {
        return disabledDate;
    }

    public Optional<InstantFilter> optionalDisabledDate() {
        return Optional.ofNullable(disabledDate);
    }

    public InstantFilter disabledDate() {
        if (disabledDate == null) {
            setDisabledDate(new InstantFilter());
        }
        return disabledDate;
    }

    public void setDisabledDate(InstantFilter disabledDate) {
        this.disabledDate = disabledDate;
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

    public LongFilter getPermissionId() {
        return permissionId;
    }

    public Optional<LongFilter> optionalPermissionId() {
        return Optional.ofNullable(permissionId);
    }

    public LongFilter permissionId() {
        if (permissionId == null) {
            setPermissionId(new LongFilter());
        }
        return permissionId;
    }

    public void setPermissionId(LongFilter permissionId) {
        this.permissionId = permissionId;
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
        final AppUserCriteria that = (AppUserCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(type, that.type) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(birthPlace, that.birthPlace) &&
            Objects.equals(gender, that.gender) &&
            Objects.equals(disabled, that.disabled) &&
            Objects.equals(disabledDate, that.disabledDate) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(companyId, that.companyId) &&
            Objects.equals(permissionId, that.permissionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            phoneNumber,
            type,
            birthDate,
            birthPlace,
            gender,
            disabled,
            disabledDate,
            userId,
            companyId,
            permissionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUserCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPhoneNumber().map(f -> "phoneNumber=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalBirthDate().map(f -> "birthDate=" + f + ", ").orElse("") +
            optionalBirthPlace().map(f -> "birthPlace=" + f + ", ").orElse("") +
            optionalGender().map(f -> "gender=" + f + ", ").orElse("") +
            optionalDisabled().map(f -> "disabled=" + f + ", ").orElse("") +
            optionalDisabledDate().map(f -> "disabledDate=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalCompanyId().map(f -> "companyId=" + f + ", ").orElse("") +
            optionalPermissionId().map(f -> "permissionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
