package com.commercial.activities.service.mapper;

import static com.commercial.activities.domain.DebtAsserts.*;
import static com.commercial.activities.domain.DebtTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DebtMapperTest {

    private DebtMapper debtMapper;

    @BeforeEach
    void setUp() {
        debtMapper = new DebtMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDebtSample1();
        var actual = debtMapper.toEntity(debtMapper.toDto(expected));
        assertDebtAllPropertiesEquals(expected, actual);
    }
}
