package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.Product;
import com.commercial.activities.domain.Supplier;
import com.commercial.activities.service.dto.CompanyDTO;
import com.commercial.activities.service.dto.ProductDTO;
import com.commercial.activities.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    @Mapping(target = "supplier", source = "supplier", qualifiedByName = "supplierId")
    ProductDTO toDto(Product s);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("supplierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SupplierDTO toDtoSupplierId(Supplier supplier);
}
