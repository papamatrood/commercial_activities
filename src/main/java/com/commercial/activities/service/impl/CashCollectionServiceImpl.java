package com.commercial.activities.service.impl;

import com.commercial.activities.domain.CashCollection;
import com.commercial.activities.repository.CashCollectionRepository;
import com.commercial.activities.service.CashCollectionService;
import com.commercial.activities.service.dto.CashCollectionDTO;
import com.commercial.activities.service.mapper.CashCollectionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.commercial.activities.domain.CashCollection}.
 */
@Service
@Transactional
public class CashCollectionServiceImpl implements CashCollectionService {

    private static final Logger LOG = LoggerFactory.getLogger(CashCollectionServiceImpl.class);

    private final CashCollectionRepository cashCollectionRepository;

    private final CashCollectionMapper cashCollectionMapper;

    public CashCollectionServiceImpl(CashCollectionRepository cashCollectionRepository, CashCollectionMapper cashCollectionMapper) {
        this.cashCollectionRepository = cashCollectionRepository;
        this.cashCollectionMapper = cashCollectionMapper;
    }

    @Override
    public CashCollectionDTO save(CashCollectionDTO cashCollectionDTO) {
        LOG.debug("Request to save CashCollection : {}", cashCollectionDTO);
        CashCollection cashCollection = cashCollectionMapper.toEntity(cashCollectionDTO);
        cashCollection = cashCollectionRepository.save(cashCollection);
        return cashCollectionMapper.toDto(cashCollection);
    }

    @Override
    public CashCollectionDTO update(CashCollectionDTO cashCollectionDTO) {
        LOG.debug("Request to update CashCollection : {}", cashCollectionDTO);
        CashCollection cashCollection = cashCollectionMapper.toEntity(cashCollectionDTO);
        cashCollection = cashCollectionRepository.save(cashCollection);
        return cashCollectionMapper.toDto(cashCollection);
    }

    @Override
    public Optional<CashCollectionDTO> partialUpdate(CashCollectionDTO cashCollectionDTO) {
        LOG.debug("Request to partially update CashCollection : {}", cashCollectionDTO);

        return cashCollectionRepository
            .findById(cashCollectionDTO.getId())
            .map(existingCashCollection -> {
                cashCollectionMapper.partialUpdate(existingCashCollection, cashCollectionDTO);

                return existingCashCollection;
            })
            .map(cashCollectionRepository::save)
            .map(cashCollectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CashCollectionDTO> findOne(Long id) {
        LOG.debug("Request to get CashCollection : {}", id);
        return cashCollectionRepository.findById(id).map(cashCollectionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CashCollection : {}", id);
        cashCollectionRepository.deleteById(id);
    }
}
