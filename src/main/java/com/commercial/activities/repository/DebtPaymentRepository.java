package com.commercial.activities.repository;

import com.commercial.activities.domain.DebtPayment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DebtPayment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DebtPaymentRepository extends JpaRepository<DebtPayment, Long>, JpaSpecificationExecutor<DebtPayment> {}
