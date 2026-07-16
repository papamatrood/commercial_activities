package com.commercial.activities.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CashCollectionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CashCollectionDTO.class);
        CashCollectionDTO cashCollectionDTO1 = new CashCollectionDTO();
        cashCollectionDTO1.setId(1L);
        CashCollectionDTO cashCollectionDTO2 = new CashCollectionDTO();
        assertThat(cashCollectionDTO1).isNotEqualTo(cashCollectionDTO2);
        cashCollectionDTO2.setId(cashCollectionDTO1.getId());
        assertThat(cashCollectionDTO1).isEqualTo(cashCollectionDTO2);
        cashCollectionDTO2.setId(2L);
        assertThat(cashCollectionDTO1).isNotEqualTo(cashCollectionDTO2);
        cashCollectionDTO1.setId(null);
        assertThat(cashCollectionDTO1).isNotEqualTo(cashCollectionDTO2);
    }
}
