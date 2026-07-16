package com.commercial.activities.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CompanySubscriptionCriteriaTest {

    @Test
    void newCompanySubscriptionCriteriaHasAllFiltersNullTest() {
        var companySubscriptionCriteria = new CompanySubscriptionCriteria();
        assertThat(companySubscriptionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void companySubscriptionCriteriaFluentMethodsCreatesFiltersTest() {
        var companySubscriptionCriteria = new CompanySubscriptionCriteria();

        setAllFilters(companySubscriptionCriteria);

        assertThat(companySubscriptionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void companySubscriptionCriteriaCopyCreatesNullFilterTest() {
        var companySubscriptionCriteria = new CompanySubscriptionCriteria();
        var copy = companySubscriptionCriteria.copy();

        assertThat(companySubscriptionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(companySubscriptionCriteria)
        );
    }

    @Test
    void companySubscriptionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var companySubscriptionCriteria = new CompanySubscriptionCriteria();
        setAllFilters(companySubscriptionCriteria);

        var copy = companySubscriptionCriteria.copy();

        assertThat(companySubscriptionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(companySubscriptionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var companySubscriptionCriteria = new CompanySubscriptionCriteria();

        assertThat(companySubscriptionCriteria).hasToString("CompanySubscriptionCriteria{}");
    }

    private static void setAllFilters(CompanySubscriptionCriteria companySubscriptionCriteria) {
        companySubscriptionCriteria.id();
        companySubscriptionCriteria.type();
        companySubscriptionCriteria.startDate();
        companySubscriptionCriteria.endDate();
        companySubscriptionCriteria.status();
        companySubscriptionCriteria.companyId();
        companySubscriptionCriteria.distinct();
    }

    private static Condition<CompanySubscriptionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCompanyId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CompanySubscriptionCriteria> copyFiltersAre(
        CompanySubscriptionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCompanyId(), copy.getCompanyId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
