package com.commercial.activities.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SaleLineCriteriaTest {

    @Test
    void newSaleLineCriteriaHasAllFiltersNullTest() {
        var saleLineCriteria = new SaleLineCriteria();
        assertThat(saleLineCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void saleLineCriteriaFluentMethodsCreatesFiltersTest() {
        var saleLineCriteria = new SaleLineCriteria();

        setAllFilters(saleLineCriteria);

        assertThat(saleLineCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void saleLineCriteriaCopyCreatesNullFilterTest() {
        var saleLineCriteria = new SaleLineCriteria();
        var copy = saleLineCriteria.copy();

        assertThat(saleLineCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(saleLineCriteria)
        );
    }

    @Test
    void saleLineCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var saleLineCriteria = new SaleLineCriteria();
        setAllFilters(saleLineCriteria);

        var copy = saleLineCriteria.copy();

        assertThat(saleLineCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(saleLineCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var saleLineCriteria = new SaleLineCriteria();

        assertThat(saleLineCriteria).hasToString("SaleLineCriteria{}");
    }

    private static void setAllFilters(SaleLineCriteria saleLineCriteria) {
        saleLineCriteria.id();
        saleLineCriteria.barcode();
        saleLineCriteria.quantity();
        saleLineCriteria.unitPrice();
        saleLineCriteria.totalPrice();
        saleLineCriteria.saleId();
        saleLineCriteria.productId();
        saleLineCriteria.distinct();
    }

    private static Condition<SaleLineCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBarcode()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getUnitPrice()) &&
                condition.apply(criteria.getTotalPrice()) &&
                condition.apply(criteria.getSaleId()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SaleLineCriteria> copyFiltersAre(SaleLineCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBarcode(), copy.getBarcode()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getUnitPrice(), copy.getUnitPrice()) &&
                condition.apply(criteria.getTotalPrice(), copy.getTotalPrice()) &&
                condition.apply(criteria.getSaleId(), copy.getSaleId()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
