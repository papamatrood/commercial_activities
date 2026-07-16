package com.commercial.activities.service;

import com.commercial.activities.service.dto.StockArrivalDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.commercial.activities.domain.StockArrival}.
 */
public interface StockArrivalService {
    /**
     * Save a stockArrival.
     *
     * @param stockArrivalDTO the entity to save.
     * @return the persisted entity.
     */
    StockArrivalDTO save(StockArrivalDTO stockArrivalDTO);

    /**
     * Updates a stockArrival.
     *
     * @param stockArrivalDTO the entity to update.
     * @return the persisted entity.
     */
    StockArrivalDTO update(StockArrivalDTO stockArrivalDTO);

    /**
     * Partially updates a stockArrival.
     *
     * @param stockArrivalDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StockArrivalDTO> partialUpdate(StockArrivalDTO stockArrivalDTO);

    /**
     * Get the "id" stockArrival.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StockArrivalDTO> findOne(Long id);

    /**
     * Delete the "id" stockArrival.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
