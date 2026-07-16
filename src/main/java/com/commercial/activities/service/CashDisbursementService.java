package com.commercial.activities.service;

import com.commercial.activities.service.dto.CashDisbursementDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.commercial.activities.domain.CashDisbursement}.
 */
public interface CashDisbursementService {
    /**
     * Save a cashDisbursement.
     *
     * @param cashDisbursementDTO the entity to save.
     * @return the persisted entity.
     */
    CashDisbursementDTO save(CashDisbursementDTO cashDisbursementDTO);

    /**
     * Updates a cashDisbursement.
     *
     * @param cashDisbursementDTO the entity to update.
     * @return the persisted entity.
     */
    CashDisbursementDTO update(CashDisbursementDTO cashDisbursementDTO);

    /**
     * Partially updates a cashDisbursement.
     *
     * @param cashDisbursementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CashDisbursementDTO> partialUpdate(CashDisbursementDTO cashDisbursementDTO);

    /**
     * Get the "id" cashDisbursement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CashDisbursementDTO> findOne(Long id);

    /**
     * Delete the "id" cashDisbursement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
