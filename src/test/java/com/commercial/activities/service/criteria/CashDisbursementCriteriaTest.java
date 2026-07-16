package com.commercial.activities.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CashDisbursementCriteriaTest {

    @Test
    void newCashDisbursementCriteriaHasAllFiltersNullTest() {
        var cashDisbursementCriteria = new CashDisbursementCriteria();
        assertThat(cashDisbursementCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void cashDisbursementCriteriaFluentMethodsCreatesFiltersTest() {
        var cashDisbursementCriteria = new CashDisbursementCriteria();

        setAllFilters(cashDisbursementCriteria);

        assertThat(cashDisbursementCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void cashDisbursementCriteriaCopyCreatesNullFilterTest() {
        var cashDisbursementCriteria = new CashDisbursementCriteria();
        var copy = cashDisbursementCriteria.copy();

        assertThat(cashDisbursementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(cashDisbursementCriteria)
        );
    }

    @Test
    void cashDisbursementCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cashDisbursementCriteria = new CashDisbursementCriteria();
        setAllFilters(cashDisbursementCriteria);

        var copy = cashDisbursementCriteria.copy();

        assertThat(cashDisbursementCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(cashDisbursementCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cashDisbursementCriteria = new CashDisbursementCriteria();

        assertThat(cashDisbursementCriteria).hasToString("CashDisbursementCriteria{}");
    }

    private static void setAllFilters(CashDisbursementCriteria cashDisbursementCriteria) {
        cashDisbursementCriteria.id();
        cashDisbursementCriteria.reason();
        cashDisbursementCriteria.amount();
        cashDisbursementCriteria.date();
        cashDisbursementCriteria.companyId();
        cashDisbursementCriteria.userId();
        cashDisbursementCriteria.distinct();
    }

    private static Condition<CashDisbursementCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReason()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getCompanyId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CashDisbursementCriteria> copyFiltersAre(
        CashDisbursementCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReason(), copy.getReason()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getCompanyId(), copy.getCompanyId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
