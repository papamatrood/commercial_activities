package com.commercial.activities.service.impl;

import com.commercial.activities.domain.SaleLine;
import com.commercial.activities.repository.SaleLineRepository;
import com.commercial.activities.service.SaleLineService;
import com.commercial.activities.service.dto.SaleLineDTO;
import com.commercial.activities.service.mapper.SaleLineMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.commercial.activities.domain.SaleLine}.
 */
@Service
@Transactional
public class SaleLineServiceImpl implements SaleLineService {

    private static final Logger LOG = LoggerFactory.getLogger(SaleLineServiceImpl.class);

    private final SaleLineRepository saleLineRepository;

    private final SaleLineMapper saleLineMapper;

    public SaleLineServiceImpl(SaleLineRepository saleLineRepository, SaleLineMapper saleLineMapper) {
        this.saleLineRepository = saleLineRepository;
        this.saleLineMapper = saleLineMapper;
    }

    @Override
    public SaleLineDTO save(SaleLineDTO saleLineDTO) {
        LOG.debug("Request to save SaleLine : {}", saleLineDTO);
        SaleLine saleLine = saleLineMapper.toEntity(saleLineDTO);
        saleLine = saleLineRepository.save(saleLine);
        return saleLineMapper.toDto(saleLine);
    }

    @Override
    public SaleLineDTO update(SaleLineDTO saleLineDTO) {
        LOG.debug("Request to update SaleLine : {}", saleLineDTO);
        SaleLine saleLine = saleLineMapper.toEntity(saleLineDTO);
        saleLine = saleLineRepository.save(saleLine);
        return saleLineMapper.toDto(saleLine);
    }

    @Override
    public Optional<SaleLineDTO> partialUpdate(SaleLineDTO saleLineDTO) {
        LOG.debug("Request to partially update SaleLine : {}", saleLineDTO);

        return saleLineRepository
            .findById(saleLineDTO.getId())
            .map(existingSaleLine -> {
                saleLineMapper.partialUpdate(existingSaleLine, saleLineDTO);

                return existingSaleLine;
            })
            .map(saleLineRepository::save)
            .map(saleLineMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SaleLineDTO> findOne(Long id) {
        LOG.debug("Request to get SaleLine : {}", id);
        return saleLineRepository.findById(id).map(saleLineMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SaleLine : {}", id);
        saleLineRepository.deleteById(id);
    }
}
