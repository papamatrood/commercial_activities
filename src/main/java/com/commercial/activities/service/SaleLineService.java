package com.commercial.activities.service;

import com.commercial.activities.service.dto.SaleLineDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.commercial.activities.domain.SaleLine}.
 */
public interface SaleLineService {
    /**
     * Save a saleLine.
     *
     * @param saleLineDTO the entity to save.
     * @return the persisted entity.
     */
    SaleLineDTO save(SaleLineDTO saleLineDTO);

    /**
     * Updates a saleLine.
     *
     * @param saleLineDTO the entity to update.
     * @return the persisted entity.
     */
    SaleLineDTO update(SaleLineDTO saleLineDTO);

    /**
     * Partially updates a saleLine.
     *
     * @param saleLineDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SaleLineDTO> partialUpdate(SaleLineDTO saleLineDTO);

    /**
     * Get the "id" saleLine.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SaleLineDTO> findOne(Long id);

    /**
     * Delete the "id" saleLine.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
