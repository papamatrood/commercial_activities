package com.commercial.activities.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SaleCriteriaTest {

    @Test
    void newSaleCriteriaHasAllFiltersNullTest() {
        var saleCriteria = new SaleCriteria();
        assertThat(saleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void saleCriteriaFluentMethodsCreatesFiltersTest() {
        var saleCriteria = new SaleCriteria();

        setAllFilters(saleCriteria);

        assertThat(saleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void saleCriteriaCopyCreatesNullFilterTest() {
        var saleCriteria = new SaleCriteria();
        var copy = saleCriteria.copy();

        assertThat(saleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(saleCriteria)
        );
    }

    @Test
    void saleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var saleCriteria = new SaleCriteria();
        setAllFilters(saleCriteria);

        var copy = saleCriteria.copy();

        assertThat(saleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(saleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var saleCriteria = new SaleCriteria();

        assertThat(saleCriteria).hasToString("SaleCriteria{}");
    }

    private static void setAllFilters(SaleCriteria saleCriteria) {
        saleCriteria.id();
        saleCriteria.date();
        saleCriteria.amountToPay();
        saleCriteria.amountPaid();
        saleCriteria.remainingAmount();
        saleCriteria.customerName();
        saleCriteria.customerCompany();
        saleCriteria.customerContact();
        saleCriteria.companyId();
        saleCriteria.sellerId();
        saleCriteria.distinct();
    }

    private static Condition<SaleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getAmountToPay()) &&
                condition.apply(criteria.getAmountPaid()) &&
                condition.apply(criteria.getRemainingAmount()) &&
                condition.apply(criteria.getCustomerName()) &&
                condition.apply(criteria.getCustomerCompany()) &&
                condition.apply(criteria.getCustomerContact()) &&
                condition.apply(criteria.getCompanyId()) &&
                condition.apply(criteria.getSellerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SaleCriteria> copyFiltersAre(SaleCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getAmountToPay(), copy.getAmountToPay()) &&
                condition.apply(criteria.getAmountPaid(), copy.getAmountPaid()) &&
                condition.apply(criteria.getRemainingAmount(), copy.getRemainingAmount()) &&
                condition.apply(criteria.getCustomerName(), copy.getCustomerName()) &&
                condition.apply(criteria.getCustomerCompany(), copy.getCustomerCompany()) &&
                condition.apply(criteria.getCustomerContact(), copy.getCustomerContact()) &&
                condition.apply(criteria.getCompanyId(), copy.getCompanyId()) &&
                condition.apply(criteria.getSellerId(), copy.getSellerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
