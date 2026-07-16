package com.commercial.activities.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompanySubscriptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompanySubscriptionDTO.class);
        CompanySubscriptionDTO companySubscriptionDTO1 = new CompanySubscriptionDTO();
        companySubscriptionDTO1.setId(1L);
        CompanySubscriptionDTO companySubscriptionDTO2 = new CompanySubscriptionDTO();
        assertThat(companySubscriptionDTO1).isNotEqualTo(companySubscriptionDTO2);
        companySubscriptionDTO2.setId(companySubscriptionDTO1.getId());
        assertThat(companySubscriptionDTO1).isEqualTo(companySubscriptionDTO2);
        companySubscriptionDTO2.setId(2L);
        assertThat(companySubscriptionDTO1).isNotEqualTo(companySubscriptionDTO2);
        companySubscriptionDTO1.setId(null);
        assertThat(companySubscriptionDTO1).isNotEqualTo(companySubscriptionDTO2);
    }
}
