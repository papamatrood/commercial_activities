package com.commercial.activities.domain;

import static com.commercial.activities.domain.CompanyTestSamples.*;
import static com.commercial.activities.domain.DebtTestSamples.*;
import static com.commercial.activities.domain.SaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DebtTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Debt.class);
        Debt debt1 = getDebtSample1();
        Debt debt2 = new Debt();
        assertThat(debt1).isNotEqualTo(debt2);

        debt2.setId(debt1.getId());
        assertThat(debt1).isEqualTo(debt2);

        debt2 = getDebtSample2();
        assertThat(debt1).isNotEqualTo(debt2);
    }

    @Test
    void companyTest() {
        Debt debt = getDebtRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        debt.setCompany(companyBack);
        assertThat(debt.getCompany()).isEqualTo(companyBack);

        debt.company(null);
        assertThat(debt.getCompany()).isNull();
    }

    @Test
    void saleTest() {
        Debt debt = getDebtRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        debt.setSale(saleBack);
        assertThat(debt.getSale()).isEqualTo(saleBack);

        debt.sale(null);
        assertThat(debt.getSale()).isNull();
    }
}
