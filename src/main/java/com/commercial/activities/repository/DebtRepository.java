package com.commercial.activities.repository;

import com.commercial.activities.domain.Debt;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Debt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DebtRepository extends JpaRepository<Debt, Long>, JpaSpecificationExecutor<Debt> {}
