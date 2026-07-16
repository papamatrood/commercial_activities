package com.commercial.activities.domain;

import static com.commercial.activities.domain.AppUserTestSamples.*;
import static com.commercial.activities.domain.CompanyTestSamples.*;
import static com.commercial.activities.domain.SaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sale.class);
        Sale sale1 = getSaleSample1();
        Sale sale2 = new Sale();
        assertThat(sale1).isNotEqualTo(sale2);

        sale2.setId(sale1.getId());
        assertThat(sale1).isEqualTo(sale2);

        sale2 = getSaleSample2();
        assertThat(sale1).isNotEqualTo(sale2);
    }

    @Test
    void companyTest() {
        Sale sale = getSaleRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        sale.setCompany(companyBack);
        assertThat(sale.getCompany()).isEqualTo(companyBack);

        sale.company(null);
        assertThat(sale.getCompany()).isNull();
    }

    @Test
    void sellerTest() {
        Sale sale = getSaleRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        sale.setSeller(appUserBack);
        assertThat(sale.getSeller()).isEqualTo(appUserBack);

        sale.seller(null);
        assertThat(sale.getSeller()).isNull();
    }
}
