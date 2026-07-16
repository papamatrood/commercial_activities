package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.Debt;
import com.commercial.activities.domain.DebtPayment;
import com.commercial.activities.service.dto.DebtDTO;
import com.commercial.activities.service.dto.DebtPaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DebtPayment} and its DTO {@link DebtPaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DebtPaymentMapper extends EntityMapper<DebtPaymentDTO, DebtPayment> {
    @Mapping(target = "debt", source = "debt", qualifiedByName = "debtId")
    DebtPaymentDTO toDto(DebtPayment s);

    @Named("debtId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DebtDTO toDtoDebtId(Debt debt);
}
