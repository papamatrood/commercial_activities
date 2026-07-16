package com.commercial.activities.repository;

import com.commercial.activities.domain.SaleLine;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SaleLine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SaleLineRepository extends JpaRepository<SaleLine, Long>, JpaSpecificationExecutor<SaleLine> {}
