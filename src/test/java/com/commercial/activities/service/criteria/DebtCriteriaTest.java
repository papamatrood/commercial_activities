package com.commercial.activities.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DebtCriteriaTest {

    @Test
    void newDebtCriteriaHasAllFiltersNullTest() {
        var debtCriteria = new DebtCriteria();
        assertThat(debtCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void debtCriteriaFluentMethodsCreatesFiltersTest() {
        var debtCriteria = new DebtCriteria();

        setAllFilters(debtCriteria);

        assertThat(debtCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void debtCriteriaCopyCreatesNullFilterTest() {
        var debtCriteria = new DebtCriteria();
        var copy = debtCriteria.copy();

        assertThat(debtCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(debtCriteria)
        );
    }

    @Test
    void debtCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var debtCriteria = new DebtCriteria();
        setAllFilters(debtCriteria);

        var copy = debtCriteria.copy();

        assertThat(debtCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(debtCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var debtCriteria = new DebtCriteria();

        assertThat(debtCriteria).hasToString("DebtCriteria{}");
    }

    private static void setAllFilters(DebtCriteria debtCriteria) {
        debtCriteria.id();
        debtCriteria.totalAmount();
        debtCriteria.amountPaid();
        debtCriteria.remainingAmount();
        debtCriteria.date();
        debtCriteria.companyId();
        debtCriteria.saleId();
        debtCriteria.distinct();
    }

    private static Condition<DebtCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getAmountPaid()) &&
                condition.apply(criteria.getRemainingAmount()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getCompanyId()) &&
                condition.apply(criteria.getSaleId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DebtCriteria> copyFiltersAre(DebtCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getAmountPaid(), copy.getAmountPaid()) &&
                condition.apply(criteria.getRemainingAmount(), copy.getRemainingAmount()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getCompanyId(), copy.getCompanyId()) &&
                condition.apply(criteria.getSaleId(), copy.getSaleId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
