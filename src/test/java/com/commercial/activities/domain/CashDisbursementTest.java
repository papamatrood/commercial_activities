package com.commercial.activities.domain;

import static com.commercial.activities.domain.AppUserTestSamples.*;
import static com.commercial.activities.domain.CashDisbursementTestSamples.*;
import static com.commercial.activities.domain.CompanyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CashDisbursementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CashDisbursement.class);
        CashDisbursement cashDisbursement1 = getCashDisbursementSample1();
        CashDisbursement cashDisbursement2 = new CashDisbursement();
        assertThat(cashDisbursement1).isNotEqualTo(cashDisbursement2);

        cashDisbursement2.setId(cashDisbursement1.getId());
        assertThat(cashDisbursement1).isEqualTo(cashDisbursement2);

        cashDisbursement2 = getCashDisbursementSample2();
        assertThat(cashDisbursement1).isNotEqualTo(cashDisbursement2);
    }

    @Test
    void companyTest() {
        CashDisbursement cashDisbursement = getCashDisbursementRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        cashDisbursement.setCompany(companyBack);
        assertThat(cashDisbursement.getCompany()).isEqualTo(companyBack);

        cashDisbursement.company(null);
        assertThat(cashDisbursement.getCompany()).isNull();
    }

    @Test
    void userTest() {
        CashDisbursement cashDisbursement = getCashDisbursementRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        cashDisbursement.setUser(appUserBack);
        assertThat(cashDisbursement.getUser()).isEqualTo(appUserBack);

        cashDisbursement.user(null);
        assertThat(cashDisbursement.getUser()).isNull();
    }
}
