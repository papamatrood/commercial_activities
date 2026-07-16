package com.commercial.activities.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StockArrivalCriteriaTest {

    @Test
    void newStockArrivalCriteriaHasAllFiltersNullTest() {
        var stockArrivalCriteria = new StockArrivalCriteria();
        assertThat(stockArrivalCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void stockArrivalCriteriaFluentMethodsCreatesFiltersTest() {
        var stockArrivalCriteria = new StockArrivalCriteria();

        setAllFilters(stockArrivalCriteria);

        assertThat(stockArrivalCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void stockArrivalCriteriaCopyCreatesNullFilterTest() {
        var stockArrivalCriteria = new StockArrivalCriteria();
        var copy = stockArrivalCriteria.copy();

        assertThat(stockArrivalCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(stockArrivalCriteria)
        );
    }

    @Test
    void stockArrivalCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var stockArrivalCriteria = new StockArrivalCriteria();
        setAllFilters(stockArrivalCriteria);

        var copy = stockArrivalCriteria.copy();

        assertThat(stockArrivalCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(stockArrivalCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var stockArrivalCriteria = new StockArrivalCriteria();

        assertThat(stockArrivalCriteria).hasToString("StockArrivalCriteria{}");
    }

    private static void setAllFilters(StockArrivalCriteria stockArrivalCriteria) {
        stockArrivalCriteria.id();
        stockArrivalCriteria.barcode();
        stockArrivalCriteria.quantity();
        stockArrivalCriteria.amount();
        stockArrivalCriteria.date();
        stockArrivalCriteria.productId();
        stockArrivalCriteria.distinct();
    }

    private static Condition<StockArrivalCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBarcode()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StockArrivalCriteria> copyFiltersAre(
        StockArrivalCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBarcode(), copy.getBarcode()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
