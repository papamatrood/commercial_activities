package com.commercial.activities.service.mapper;

import static com.commercial.activities.domain.CashDisbursementAsserts.*;
import static com.commercial.activities.domain.CashDisbursementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CashDisbursementMapperTest {

    private CashDisbursementMapper cashDisbursementMapper;

    @BeforeEach
    void setUp() {
        cashDisbursementMapper = new CashDisbursementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCashDisbursementSample1();
        var actual = cashDisbursementMapper.toEntity(cashDisbursementMapper.toDto(expected));
        assertCashDisbursementAllPropertiesEquals(expected, actual);
    }
}
