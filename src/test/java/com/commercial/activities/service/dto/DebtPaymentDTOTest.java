package com.commercial.activities.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DebtPaymentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DebtPaymentDTO.class);
        DebtPaymentDTO debtPaymentDTO1 = new DebtPaymentDTO();
        debtPaymentDTO1.setId(1L);
        DebtPaymentDTO debtPaymentDTO2 = new DebtPaymentDTO();
        assertThat(debtPaymentDTO1).isNotEqualTo(debtPaymentDTO2);
        debtPaymentDTO2.setId(debtPaymentDTO1.getId());
        assertThat(debtPaymentDTO1).isEqualTo(debtPaymentDTO2);
        debtPaymentDTO2.setId(2L);
        assertThat(debtPaymentDTO1).isNotEqualTo(debtPaymentDTO2);
        debtPaymentDTO1.setId(null);
        assertThat(debtPaymentDTO1).isNotEqualTo(debtPaymentDTO2);
    }
}
