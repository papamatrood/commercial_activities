package com.commercial.activities.service;

import com.commercial.activities.service.dto.DebtDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.commercial.activities.domain.Debt}.
 */
public interface DebtService {
    /**
     * Save a debt.
     *
     * @param debtDTO the entity to save.
     * @return the persisted entity.
     */
    DebtDTO save(DebtDTO debtDTO);

    /**
     * Updates a debt.
     *
     * @param debtDTO the entity to update.
     * @return the persisted entity.
     */
    DebtDTO update(DebtDTO debtDTO);

    /**
     * Partially updates a debt.
     *
     * @param debtDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DebtDTO> partialUpdate(DebtDTO debtDTO);

    /**
     * Get the "id" debt.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DebtDTO> findOne(Long id);

    /**
     * Delete the "id" debt.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
