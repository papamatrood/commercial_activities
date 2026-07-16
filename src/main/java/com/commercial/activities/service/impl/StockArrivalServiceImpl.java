package com.commercial.activities.service.impl;

import com.commercial.activities.domain.StockArrival;
import com.commercial.activities.repository.StockArrivalRepository;
import com.commercial.activities.service.StockArrivalService;
import com.commercial.activities.service.dto.StockArrivalDTO;
import com.commercial.activities.service.mapper.StockArrivalMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.commercial.activities.domain.StockArrival}.
 */
@Service
@Transactional
public class StockArrivalServiceImpl implements StockArrivalService {

    private static final Logger LOG = LoggerFactory.getLogger(StockArrivalServiceImpl.class);

    private final StockArrivalRepository stockArrivalRepository;

    private final StockArrivalMapper stockArrivalMapper;

    public StockArrivalServiceImpl(StockArrivalRepository stockArrivalRepository, StockArrivalMapper stockArrivalMapper) {
        this.stockArrivalRepository = stockArrivalRepository;
        this.stockArrivalMapper = stockArrivalMapper;
    }

    @Override
    public StockArrivalDTO save(StockArrivalDTO stockArrivalDTO) {
        LOG.debug("Request to save StockArrival : {}", stockArrivalDTO);
        StockArrival stockArrival = stockArrivalMapper.toEntity(stockArrivalDTO);
        stockArrival = stockArrivalRepository.save(stockArrival);
        return stockArrivalMapper.toDto(stockArrival);
    }

    @Override
    public StockArrivalDTO update(StockArrivalDTO stockArrivalDTO) {
        LOG.debug("Request to update StockArrival : {}", stockArrivalDTO);
        StockArrival stockArrival = stockArrivalMapper.toEntity(stockArrivalDTO);
        stockArrival = stockArrivalRepository.save(stockArrival);
        return stockArrivalMapper.toDto(stockArrival);
    }

    @Override
    public Optional<StockArrivalDTO> partialUpdate(StockArrivalDTO stockArrivalDTO) {
        LOG.debug("Request to partially update StockArrival : {}", stockArrivalDTO);

        return stockArrivalRepository
            .findById(stockArrivalDTO.getId())
            .map(existingStockArrival -> {
                stockArrivalMapper.partialUpdate(existingStockArrival, stockArrivalDTO);

                return existingStockArrival;
            })
            .map(stockArrivalRepository::save)
            .map(stockArrivalMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StockArrivalDTO> findOne(Long id) {
        LOG.debug("Request to get StockArrival : {}", id);
        return stockArrivalRepository.findById(id).map(stockArrivalMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StockArrival : {}", id);
        stockArrivalRepository.deleteById(id);
    }
}
