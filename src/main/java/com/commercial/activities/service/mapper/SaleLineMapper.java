package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.Product;
import com.commercial.activities.domain.Sale;
import com.commercial.activities.domain.SaleLine;
import com.commercial.activities.service.dto.ProductDTO;
import com.commercial.activities.service.dto.SaleDTO;
import com.commercial.activities.service.dto.SaleLineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SaleLine} and its DTO {@link SaleLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleLineMapper extends EntityMapper<SaleLineDTO, SaleLine> {
    @Mapping(target = "sale", source = "sale", qualifiedByName = "saleId")
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    SaleLineDTO toDto(SaleLine s);

    @Named("saleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleDTO toDtoSaleId(Sale sale);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);
}
