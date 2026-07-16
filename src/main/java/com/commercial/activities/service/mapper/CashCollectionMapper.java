package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.CashCollection;
import com.commercial.activities.domain.Company;
import com.commercial.activities.service.dto.AppUserDTO;
import com.commercial.activities.service.dto.CashCollectionDTO;
import com.commercial.activities.service.dto.CompanyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CashCollection} and its DTO {@link CashCollectionDTO}.
 */
@Mapper(componentModel = "spring")
public interface CashCollectionMapper extends EntityMapper<CashCollectionDTO, CashCollection> {
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    @Mapping(target = "user", source = "user", qualifiedByName = "appUserId")
    CashCollectionDTO toDto(CashCollection s);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("appUserId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppUserDTO toDtoAppUserId(AppUser appUser);
}
