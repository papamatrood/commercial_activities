package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.CompanySubscription;
import com.commercial.activities.service.dto.CompanyDTO;
import com.commercial.activities.service.dto.CompanySubscriptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CompanySubscription} and its DTO {@link CompanySubscriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompanySubscriptionMapper extends EntityMapper<CompanySubscriptionDTO, CompanySubscription> {
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    CompanySubscriptionDTO toDto(CompanySubscription s);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);
}
