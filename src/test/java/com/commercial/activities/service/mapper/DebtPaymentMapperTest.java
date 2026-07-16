package com.commercial.activities.service.mapper;

import static com.commercial.activities.domain.DebtPaymentAsserts.*;
import static com.commercial.activities.domain.DebtPaymentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DebtPaymentMapperTest {

    private DebtPaymentMapper debtPaymentMapper;

    @BeforeEach
    void setUp() {
        debtPaymentMapper = new DebtPaymentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDebtPaymentSample1();
        var actual = debtPaymentMapper.toEntity(debtPaymentMapper.toDto(expected));
        assertDebtPaymentAllPropertiesEquals(expected, actual);
    }
}
