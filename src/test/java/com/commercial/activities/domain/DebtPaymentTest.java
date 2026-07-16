package com.commercial.activities.domain;

import static com.commercial.activities.domain.DebtPaymentTestSamples.*;
import static com.commercial.activities.domain.DebtTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DebtPaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DebtPayment.class);
        DebtPayment debtPayment1 = getDebtPaymentSample1();
        DebtPayment debtPayment2 = new DebtPayment();
        assertThat(debtPayment1).isNotEqualTo(debtPayment2);

        debtPayment2.setId(debtPayment1.getId());
        assertThat(debtPayment1).isEqualTo(debtPayment2);

        debtPayment2 = getDebtPaymentSample2();
        assertThat(debtPayment1).isNotEqualTo(debtPayment2);
    }

    @Test
    void debtTest() {
        DebtPayment debtPayment = getDebtPaymentRandomSampleGenerator();
        Debt debtBack = getDebtRandomSampleGenerator();

        debtPayment.setDebt(debtBack);
        assertThat(debtPayment.getDebt()).isEqualTo(debtBack);

        debtPayment.debt(null);
        assertThat(debtPayment.getDebt()).isNull();
    }
}
