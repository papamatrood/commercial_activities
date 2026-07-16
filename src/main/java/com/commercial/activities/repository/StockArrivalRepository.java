package com.commercial.activities.repository;

import com.commercial.activities.domain.StockArrival;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockArrival entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockArrivalRepository extends JpaRepository<StockArrival, Long>, JpaSpecificationExecutor<StockArrival> {}
