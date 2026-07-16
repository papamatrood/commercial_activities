package com.commercial.activities.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AppUserCriteriaTest {

    @Test
    void newAppUserCriteriaHasAllFiltersNullTest() {
        var appUserCriteria = new AppUserCriteria();
        assertThat(appUserCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void appUserCriteriaFluentMethodsCreatesFiltersTest() {
        var appUserCriteria = new AppUserCriteria();

        setAllFilters(appUserCriteria);

        assertThat(appUserCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void appUserCriteriaCopyCreatesNullFilterTest() {
        var appUserCriteria = new AppUserCriteria();
        var copy = appUserCriteria.copy();

        assertThat(appUserCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(appUserCriteria)
        );
    }

    @Test
    void appUserCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var appUserCriteria = new AppUserCriteria();
        setAllFilters(appUserCriteria);

        var copy = appUserCriteria.copy();

        assertThat(appUserCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(appUserCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var appUserCriteria = new AppUserCriteria();

        assertThat(appUserCriteria).hasToString("AppUserCriteria{}");
    }

    private static void setAllFilters(AppUserCriteria appUserCriteria) {
        appUserCriteria.id();
        appUserCriteria.phoneNumber();
        appUserCriteria.type();
        appUserCriteria.birthDate();
        appUserCriteria.birthPlace();
        appUserCriteria.gender();
        appUserCriteria.disabled();
        appUserCriteria.disabledDate();
        appUserCriteria.userId();
        appUserCriteria.companyId();
        appUserCriteria.permissionId();
        appUserCriteria.distinct();
    }

    private static Condition<AppUserCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getBirthDate()) &&
                condition.apply(criteria.getBirthPlace()) &&
                condition.apply(criteria.getGender()) &&
                condition.apply(criteria.getDisabled()) &&
                condition.apply(criteria.getDisabledDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getCompanyId()) &&
                condition.apply(criteria.getPermissionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AppUserCriteria> copyFiltersAre(AppUserCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getBirthDate(), copy.getBirthDate()) &&
                condition.apply(criteria.getBirthPlace(), copy.getBirthPlace()) &&
                condition.apply(criteria.getGender(), copy.getGender()) &&
                condition.apply(criteria.getDisabled(), copy.getDisabled()) &&
                condition.apply(criteria.getDisabledDate(), copy.getDisabledDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getCompanyId(), copy.getCompanyId()) &&
                condition.apply(criteria.getPermissionId(), copy.getPermissionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
