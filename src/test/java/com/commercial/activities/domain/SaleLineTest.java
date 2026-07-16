package com.commercial.activities.domain;

import static com.commercial.activities.domain.ProductTestSamples.*;
import static com.commercial.activities.domain.SaleLineTestSamples.*;
import static com.commercial.activities.domain.SaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleLine.class);
        SaleLine saleLine1 = getSaleLineSample1();
        SaleLine saleLine2 = new SaleLine();
        assertThat(saleLine1).isNotEqualTo(saleLine2);

        saleLine2.setId(saleLine1.getId());
        assertThat(saleLine1).isEqualTo(saleLine2);

        saleLine2 = getSaleLineSample2();
        assertThat(saleLine1).isNotEqualTo(saleLine2);
    }

    @Test
    void saleTest() {
        SaleLine saleLine = getSaleLineRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        saleLine.setSale(saleBack);
        assertThat(saleLine.getSale()).isEqualTo(saleBack);

        saleLine.sale(null);
        assertThat(saleLine.getSale()).isNull();
    }

    @Test
    void productTest() {
        SaleLine saleLine = getSaleLineRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        saleLine.setProduct(productBack);
        assertThat(saleLine.getProduct()).isEqualTo(productBack);

        saleLine.product(null);
        assertThat(saleLine.getProduct()).isNull();
    }
}
