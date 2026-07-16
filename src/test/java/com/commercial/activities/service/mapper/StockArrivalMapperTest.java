package com.commercial.activities.service.mapper;

import static com.commercial.activities.domain.StockArrivalAsserts.*;
import static com.commercial.activities.domain.StockArrivalTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockArrivalMapperTest {

    private StockArrivalMapper stockArrivalMapper;

    @BeforeEach
    void setUp() {
        stockArrivalMapper = new StockArrivalMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStockArrivalSample1();
        var actual = stockArrivalMapper.toEntity(stockArrivalMapper.toDto(expected));
        assertStockArrivalAllPropertiesEquals(expected, actual);
    }
}
