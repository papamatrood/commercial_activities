package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.Sale;
import com.commercial.activities.service.dto.AppUserDTO;
import com.commercial.activities.service.dto.CompanyDTO;
import com.commercial.activities.service.dto.SaleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sale} and its DTO {@link SaleDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleMapper extends EntityMapper<SaleDTO, Sale> {
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    @Mapping(target = "seller", source = "seller", qualifiedByName = "appUserId")
    SaleDTO toDto(Sale s);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("appUserId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppUserDTO toDtoAppUserId(AppUser appUser);
}
