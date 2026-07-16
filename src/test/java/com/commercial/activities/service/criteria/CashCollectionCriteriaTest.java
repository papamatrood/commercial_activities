package com.commercial.activities.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CashCollectionCriteriaTest {

    @Test
    void newCashCollectionCriteriaHasAllFiltersNullTest() {
        var cashCollectionCriteria = new CashCollectionCriteria();
        assertThat(cashCollectionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void cashCollectionCriteriaFluentMethodsCreatesFiltersTest() {
        var cashCollectionCriteria = new CashCollectionCriteria();

        setAllFilters(cashCollectionCriteria);

        assertThat(cashCollectionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void cashCollectionCriteriaCopyCreatesNullFilterTest() {
        var cashCollectionCriteria = new CashCollectionCriteria();
        var copy = cashCollectionCriteria.copy();

        assertThat(cashCollectionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(cashCollectionCriteria)
        );
    }

    @Test
    void cashCollectionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cashCollectionCriteria = new CashCollectionCriteria();
        setAllFilters(cashCollectionCriteria);

        var copy = cashCollectionCriteria.copy();

        assertThat(cashCollectionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(cashCollectionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cashCollectionCriteria = new CashCollectionCriteria();

        assertThat(cashCollectionCriteria).hasToString("CashCollectionCriteria{}");
    }

    private static void setAllFilters(CashCollectionCriteria cashCollectionCriteria) {
        cashCollectionCriteria.id();
        cashCollectionCriteria.date();
        cashCollectionCriteria.amount();
        cashCollectionCriteria.companyId();
        cashCollectionCriteria.userId();
        cashCollectionCriteria.distinct();
    }

    private static Condition<CashCollectionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getCompanyId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CashCollectionCriteria> copyFiltersAre(
        CashCollectionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getCompanyId(), copy.getCompanyId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
