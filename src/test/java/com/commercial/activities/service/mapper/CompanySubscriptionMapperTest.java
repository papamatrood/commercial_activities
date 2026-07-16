package com.commercial.activities.service.mapper;

import static com.commercial.activities.domain.CompanySubscriptionAsserts.*;
import static com.commercial.activities.domain.CompanySubscriptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompanySubscriptionMapperTest {

    private CompanySubscriptionMapper companySubscriptionMapper;

    @BeforeEach
    void setUp() {
        companySubscriptionMapper = new CompanySubscriptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCompanySubscriptionSample1();
        var actual = companySubscriptionMapper.toEntity(companySubscriptionMapper.toDto(expected));
        assertCompanySubscriptionAllPropertiesEquals(expected, actual);
    }
}
