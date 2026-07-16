package com.commercial.activities.repository;

import com.commercial.activities.domain.CashDisbursement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CashDisbursement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CashDisbursementRepository extends JpaRepository<CashDisbursement, Long>, JpaSpecificationExecutor<CashDisbursement> {}
