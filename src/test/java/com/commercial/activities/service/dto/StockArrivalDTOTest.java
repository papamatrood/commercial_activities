package com.commercial.activities.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockArrivalDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockArrivalDTO.class);
        StockArrivalDTO stockArrivalDTO1 = new StockArrivalDTO();
        stockArrivalDTO1.setId(1L);
        StockArrivalDTO stockArrivalDTO2 = new StockArrivalDTO();
        assertThat(stockArrivalDTO1).isNotEqualTo(stockArrivalDTO2);
        stockArrivalDTO2.setId(stockArrivalDTO1.getId());
        assertThat(stockArrivalDTO1).isEqualTo(stockArrivalDTO2);
        stockArrivalDTO2.setId(2L);
        assertThat(stockArrivalDTO1).isNotEqualTo(stockArrivalDTO2);
        stockArrivalDTO1.setId(null);
        assertThat(stockArrivalDTO1).isNotEqualTo(stockArrivalDTO2);
    }
}
