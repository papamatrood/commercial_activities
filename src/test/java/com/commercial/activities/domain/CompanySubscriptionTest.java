package com.commercial.activities.domain;

import static com.commercial.activities.domain.CompanySubscriptionTestSamples.*;
import static com.commercial.activities.domain.CompanyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompanySubscriptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompanySubscription.class);
        CompanySubscription companySubscription1 = getCompanySubscriptionSample1();
        CompanySubscription companySubscription2 = new CompanySubscription();
        assertThat(companySubscription1).isNotEqualTo(companySubscription2);

        companySubscription2.setId(companySubscription1.getId());
        assertThat(companySubscription1).isEqualTo(companySubscription2);

        companySubscription2 = getCompanySubscriptionSample2();
        assertThat(companySubscription1).isNotEqualTo(companySubscription2);
    }

    @Test
    void companyTest() {
        CompanySubscription companySubscription = getCompanySubscriptionRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        companySubscription.setCompany(companyBack);
        assertThat(companySubscription.getCompany()).isEqualTo(companyBack);

        companySubscription.company(null);
        assertThat(companySubscription.getCompany()).isNull();
    }
}
