package com.commercial.activities.service.impl;

import com.commercial.activities.domain.Debt;
import com.commercial.activities.repository.DebtRepository;
import com.commercial.activities.service.DebtService;
import com.commercial.activities.service.dto.DebtDTO;
import com.commercial.activities.service.mapper.DebtMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.commercial.activities.domain.Debt}.
 */
@Service
@Transactional
public class DebtServiceImpl implements DebtService {

    private static final Logger LOG = LoggerFactory.getLogger(DebtServiceImpl.class);

    private final DebtRepository debtRepository;

    private final DebtMapper debtMapper;

    public DebtServiceImpl(DebtRepository debtRepository, DebtMapper debtMapper) {
        this.debtRepository = debtRepository;
        this.debtMapper = debtMapper;
    }

    @Override
    public DebtDTO save(DebtDTO debtDTO) {
        LOG.debug("Request to save Debt : {}", debtDTO);
        Debt debt = debtMapper.toEntity(debtDTO);
        debt = debtRepository.save(debt);
        return debtMapper.toDto(debt);
    }

    @Override
    public DebtDTO update(DebtDTO debtDTO) {
        LOG.debug("Request to update Debt : {}", debtDTO);
        Debt debt = debtMapper.toEntity(debtDTO);
        debt = debtRepository.save(debt);
        return debtMapper.toDto(debt);
    }

    @Override
    public Optional<DebtDTO> partialUpdate(DebtDTO debtDTO) {
        LOG.debug("Request to partially update Debt : {}", debtDTO);

        return debtRepository
            .findById(debtDTO.getId())
            .map(existingDebt -> {
                debtMapper.partialUpdate(existingDebt, debtDTO);

                return existingDebt;
            })
            .map(debtRepository::save)
            .map(debtMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DebtDTO> findOne(Long id) {
        LOG.debug("Request to get Debt : {}", id);
        return debtRepository.findById(id).map(debtMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Debt : {}", id);
        debtRepository.deleteById(id);
    }
}
