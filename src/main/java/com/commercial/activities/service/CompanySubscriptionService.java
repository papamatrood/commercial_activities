package com.commercial.activities.service;

import com.commercial.activities.service.dto.CompanySubscriptionDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.commercial.activities.domain.CompanySubscription}.
 */
public interface CompanySubscriptionService {
    /**
     * Save a companySubscription.
     *
     * @param companySubscriptionDTO the entity to save.
     * @return the persisted entity.
     */
    CompanySubscriptionDTO save(CompanySubscriptionDTO companySubscriptionDTO);

    /**
     * Updates a companySubscription.
     *
     * @param companySubscriptionDTO the entity to update.
     * @return the persisted entity.
     */
    CompanySubscriptionDTO update(CompanySubscriptionDTO companySubscriptionDTO);

    /**
     * Partially updates a companySubscription.
     *
     * @param companySubscriptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CompanySubscriptionDTO> partialUpdate(CompanySubscriptionDTO companySubscriptionDTO);

    /**
     * Get the "id" companySubscription.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CompanySubscriptionDTO> findOne(Long id);

    /**
     * Delete the "id" companySubscription.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
