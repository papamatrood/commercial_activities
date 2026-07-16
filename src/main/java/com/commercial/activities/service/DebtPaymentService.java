package com.commercial.activities.service;

import com.commercial.activities.service.dto.DebtPaymentDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.commercial.activities.domain.DebtPayment}.
 */
public interface DebtPaymentService {
    /**
     * Save a debtPayment.
     *
     * @param debtPaymentDTO the entity to save.
     * @return the persisted entity.
     */
    DebtPaymentDTO save(DebtPaymentDTO debtPaymentDTO);

    /**
     * Updates a debtPayment.
     *
     * @param debtPaymentDTO the entity to update.
     * @return the persisted entity.
     */
    DebtPaymentDTO update(DebtPaymentDTO debtPaymentDTO);

    /**
     * Partially updates a debtPayment.
     *
     * @param debtPaymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DebtPaymentDTO> partialUpdate(DebtPaymentDTO debtPaymentDTO);

    /**
     * Get the "id" debtPayment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DebtPaymentDTO> findOne(Long id);

    /**
     * Delete the "id" debtPayment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
