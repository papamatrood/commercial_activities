package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.Debt;
import com.commercial.activities.domain.Sale;
import com.commercial.activities.service.dto.CompanyDTO;
import com.commercial.activities.service.dto.DebtDTO;
import com.commercial.activities.service.dto.SaleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Debt} and its DTO {@link DebtDTO}.
 */
@Mapper(componentModel = "spring")
public interface DebtMapper extends EntityMapper<DebtDTO, Debt> {
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    @Mapping(target = "sale", source = "sale", qualifiedByName = "saleId")
    DebtDTO toDto(Debt s);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("saleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleDTO toDtoSaleId(Sale sale);
}
