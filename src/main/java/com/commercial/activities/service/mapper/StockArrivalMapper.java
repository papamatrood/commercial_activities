package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.Product;
import com.commercial.activities.domain.StockArrival;
import com.commercial.activities.service.dto.ProductDTO;
import com.commercial.activities.service.dto.StockArrivalDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockArrival} and its DTO {@link StockArrivalDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockArrivalMapper extends EntityMapper<StockArrivalDTO, StockArrival> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    StockArrivalDTO toDto(StockArrival s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);
}
