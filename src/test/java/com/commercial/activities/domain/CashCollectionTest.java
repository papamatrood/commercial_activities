package com.commercial.activities.domain;

import static com.commercial.activities.domain.AppUserTestSamples.*;
import static com.commercial.activities.domain.CashCollectionTestSamples.*;
import static com.commercial.activities.domain.CompanyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CashCollectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CashCollection.class);
        CashCollection cashCollection1 = getCashCollectionSample1();
        CashCollection cashCollection2 = new CashCollection();
        assertThat(cashCollection1).isNotEqualTo(cashCollection2);

        cashCollection2.setId(cashCollection1.getId());
        assertThat(cashCollection1).isEqualTo(cashCollection2);

        cashCollection2 = getCashCollectionSample2();
        assertThat(cashCollection1).isNotEqualTo(cashCollection2);
    }

    @Test
    void companyTest() {
        CashCollection cashCollection = getCashCollectionRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        cashCollection.setCompany(companyBack);
        assertThat(cashCollection.getCompany()).isEqualTo(companyBack);

        cashCollection.company(null);
        assertThat(cashCollection.getCompany()).isNull();
    }

    @Test
    void userTest() {
        CashCollection cashCollection = getCashCollectionRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        cashCollection.setUser(appUserBack);
        assertThat(cashCollection.getUser()).isEqualTo(appUserBack);

        cashCollection.user(null);
        assertThat(cashCollection.getUser()).isNull();
    }
}
