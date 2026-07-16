package com.commercial.activities.service.mapper;

import static com.commercial.activities.domain.SaleLineAsserts.*;
import static com.commercial.activities.domain.SaleLineTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaleLineMapperTest {

    private SaleLineMapper saleLineMapper;

    @BeforeEach
    void setUp() {
        saleLineMapper = new SaleLineMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSaleLineSample1();
        var actual = saleLineMapper.toEntity(saleLineMapper.toDto(expected));
        assertSaleLineAllPropertiesEquals(expected, actual);
    }
}
