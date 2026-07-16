package com.commercial.activities.repository;

import com.commercial.activities.domain.CashCollection;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CashCollection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CashCollectionRepository extends JpaRepository<CashCollection, Long>, JpaSpecificationExecutor<CashCollection> {}
