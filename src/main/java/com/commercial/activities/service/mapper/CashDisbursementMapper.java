package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.CashDisbursement;
import com.commercial.activities.domain.Company;
import com.commercial.activities.service.dto.AppUserDTO;
import com.commercial.activities.service.dto.CashDisbursementDTO;
import com.commercial.activities.service.dto.CompanyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CashDisbursement} and its DTO {@link CashDisbursementDTO}.
 */
@Mapper(componentModel = "spring")
public interface CashDisbursementMapper extends EntityMapper<CashDisbursementDTO, CashDisbursement> {
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    @Mapping(target = "user", source = "user", qualifiedByName = "appUserId")
    CashDisbursementDTO toDto(CashDisbursement s);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("appUserId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppUserDTO toDtoAppUserId(AppUser appUser);
}
