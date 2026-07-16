package com.commercial.activities.service.mapper;

import static com.commercial.activities.domain.CashCollectionAsserts.*;
import static com.commercial.activities.domain.CashCollectionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CashCollectionMapperTest {

    private CashCollectionMapper cashCollectionMapper;

    @BeforeEach
    void setUp() {
        cashCollectionMapper = new CashCollectionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCashCollectionSample1();
        var actual = cashCollectionMapper.toEntity(cashCollectionMapper.toDto(expected));
        assertCashCollectionAllPropertiesEquals(expected, actual);
    }
}
