package com.commercial.activities.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DebtPaymentCriteriaTest {

    @Test
    void newDebtPaymentCriteriaHasAllFiltersNullTest() {
        var debtPaymentCriteria = new DebtPaymentCriteria();
        assertThat(debtPaymentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void debtPaymentCriteriaFluentMethodsCreatesFiltersTest() {
        var debtPaymentCriteria = new DebtPaymentCriteria();

        setAllFilters(debtPaymentCriteria);

        assertThat(debtPaymentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void debtPaymentCriteriaCopyCreatesNullFilterTest() {
        var debtPaymentCriteria = new DebtPaymentCriteria();
        var copy = debtPaymentCriteria.copy();

        assertThat(debtPaymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(debtPaymentCriteria)
        );
    }

    @Test
    void debtPaymentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var debtPaymentCriteria = new DebtPaymentCriteria();
        setAllFilters(debtPaymentCriteria);

        var copy = debtPaymentCriteria.copy();

        assertThat(debtPaymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(debtPaymentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var debtPaymentCriteria = new DebtPaymentCriteria();

        assertThat(debtPaymentCriteria).hasToString("DebtPaymentCriteria{}");
    }

    private static void setAllFilters(DebtPaymentCriteria debtPaymentCriteria) {
        debtPaymentCriteria.id();
        debtPaymentCriteria.amountPaid();
        debtPaymentCriteria.remainingAmount();
        debtPaymentCriteria.date();
        debtPaymentCriteria.debtId();
        debtPaymentCriteria.distinct();
    }

    private static Condition<DebtPaymentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAmountPaid()) &&
                condition.apply(criteria.getRemainingAmount()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getDebtId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DebtPaymentCriteria> copyFiltersAre(DebtPaymentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAmountPaid(), copy.getAmountPaid()) &&
                condition.apply(criteria.getRemainingAmount(), copy.getRemainingAmount()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getDebtId(), copy.getDebtId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
