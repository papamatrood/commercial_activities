package com.commercial.activities.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CashDisbursementDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CashDisbursementDTO.class);
        CashDisbursementDTO cashDisbursementDTO1 = new CashDisbursementDTO();
        cashDisbursementDTO1.setId(1L);
        CashDisbursementDTO cashDisbursementDTO2 = new CashDisbursementDTO();
        assertThat(cashDisbursementDTO1).isNotEqualTo(cashDisbursementDTO2);
        cashDisbursementDTO2.setId(cashDisbursementDTO1.getId());
        assertThat(cashDisbursementDTO1).isEqualTo(cashDisbursementDTO2);
        cashDisbursementDTO2.setId(2L);
        assertThat(cashDisbursementDTO1).isNotEqualTo(cashDisbursementDTO2);
        cashDisbursementDTO1.setId(null);
        assertThat(cashDisbursementDTO1).isNotEqualTo(cashDisbursementDTO2);
    }
}
