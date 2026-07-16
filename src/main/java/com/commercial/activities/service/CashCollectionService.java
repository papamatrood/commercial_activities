package com.commercial.activities.service;

import com.commercial.activities.service.dto.CashCollectionDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.commercial.activities.domain.CashCollection}.
 */
public interface CashCollectionService {
    /**
     * Save a cashCollection.
     *
     * @param cashCollectionDTO the entity to save.
     * @return the persisted entity.
     */
    CashCollectionDTO save(CashCollectionDTO cashCollectionDTO);

    /**
     * Updates a cashCollection.
     *
     * @param cashCollectionDTO the entity to update.
     * @return the persisted entity.
     */
    CashCollectionDTO update(CashCollectionDTO cashCollectionDTO);

    /**
     * Partially updates a cashCollection.
     *
     * @param cashCollectionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CashCollectionDTO> partialUpdate(CashCollectionDTO cashCollectionDTO);

    /**
     * Get the "id" cashCollection.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CashCollectionDTO> findOne(Long id);

    /**
     * Delete the "id" cashCollection.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
