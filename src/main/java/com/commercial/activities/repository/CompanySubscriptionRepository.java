package com.commercial.activities.repository;

import com.commercial.activities.domain.CompanySubscription;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CompanySubscription entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanySubscriptionRepository
    extends JpaRepository<CompanySubscription, Long>, JpaSpecificationExecutor<CompanySubscription> {}
