package com.commercial.activities.service.impl;

import com.commercial.activities.domain.CashDisbursement;
import com.commercial.activities.repository.CashDisbursementRepository;
import com.commercial.activities.service.CashDisbursementService;
import com.commercial.activities.service.dto.CashDisbursementDTO;
import com.commercial.activities.service.mapper.CashDisbursementMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.commercial.activities.domain.CashDisbursement}.
 */
@Service
@Transactional
public class CashDisbursementServiceImpl implements CashDisbursementService {

    private static final Logger LOG = LoggerFactory.getLogger(CashDisbursementServiceImpl.class);

    private final CashDisbursementRepository cashDisbursementRepository;

    private final CashDisbursementMapper cashDisbursementMapper;

    public CashDisbursementServiceImpl(
        CashDisbursementRepository cashDisbursementRepository,
        CashDisbursementMapper cashDisbursementMapper
    ) {
        this.cashDisbursementRepository = cashDisbursementRepository;
        this.cashDisbursementMapper = cashDisbursementMapper;
    }

    @Override
    public CashDisbursementDTO save(CashDisbursementDTO cashDisbursementDTO) {
        LOG.debug("Request to save CashDisbursement : {}", cashDisbursementDTO);
        CashDisbursement cashDisbursement = cashDisbursementMapper.toEntity(cashDisbursementDTO);
        cashDisbursement = cashDisbursementRepository.save(cashDisbursement);
        return cashDisbursementMapper.toDto(cashDisbursement);
    }

    @Override
    public CashDisbursementDTO update(CashDisbursementDTO cashDisbursementDTO) {
        LOG.debug("Request to update CashDisbursement : {}", cashDisbursementDTO);
        CashDisbursement cashDisbursement = cashDisbursementMapper.toEntity(cashDisbursementDTO);
        cashDisbursement = cashDisbursementRepository.save(cashDisbursement);
        return cashDisbursementMapper.toDto(cashDisbursement);
    }

    @Override
    public Optional<CashDisbursementDTO> partialUpdate(CashDisbursementDTO cashDisbursementDTO) {
        LOG.debug("Request to partially update CashDisbursement : {}", cashDisbursementDTO);

        return cashDisbursementRepository
            .findById(cashDisbursementDTO.getId())
            .map(existingCashDisbursement -> {
                cashDisbursementMapper.partialUpdate(existingCashDisbursement, cashDisbursementDTO);

                return existingCashDisbursement;
            })
            .map(cashDisbursementRepository::save)
            .map(cashDisbursementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CashDisbursementDTO> findOne(Long id) {
        LOG.debug("Request to get CashDisbursement : {}", id);
        return cashDisbursementRepository.findById(id).map(cashDisbursementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CashDisbursement : {}", id);
        cashDisbursementRepository.deleteById(id);
    }
}
