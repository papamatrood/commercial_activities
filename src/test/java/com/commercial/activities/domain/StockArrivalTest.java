package com.commercial.activities.domain;

import static com.commercial.activities.domain.ProductTestSamples.*;
import static com.commercial.activities.domain.StockArrivalTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockArrivalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockArrival.class);
        StockArrival stockArrival1 = getStockArrivalSample1();
        StockArrival stockArrival2 = new StockArrival();
        assertThat(stockArrival1).isNotEqualTo(stockArrival2);

        stockArrival2.setId(stockArrival1.getId());
        assertThat(stockArrival1).isEqualTo(stockArrival2);

        stockArrival2 = getStockArrivalSample2();
        assertThat(stockArrival1).isNotEqualTo(stockArrival2);
    }

    @Test
    void productTest() {
        StockArrival stockArrival = getStockArrivalRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        stockArrival.setProduct(productBack);
        assertThat(stockArrival.getProduct()).isEqualTo(productBack);

        stockArrival.product(null);
        assertThat(stockArrival.getProduct()).isNull();
    }
}
