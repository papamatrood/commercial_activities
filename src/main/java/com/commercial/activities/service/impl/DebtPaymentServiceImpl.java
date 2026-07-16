package com.commercial.activities.service.impl;

import com.commercial.activities.domain.DebtPayment;
import com.commercial.activities.repository.DebtPaymentRepository;
import com.commercial.activities.service.DebtPaymentService;
import com.commercial.activities.service.dto.DebtPaymentDTO;
import com.commercial.activities.service.mapper.DebtPaymentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.commercial.activities.domain.DebtPayment}.
 */
@Service
@Transactional
public class DebtPaymentServiceImpl implements DebtPaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(DebtPaymentServiceImpl.class);

    private final DebtPaymentRepository debtPaymentRepository;

    private final DebtPaymentMapper debtPaymentMapper;

    public DebtPaymentServiceImpl(DebtPaymentRepository debtPaymentRepository, DebtPaymentMapper debtPaymentMapper) {
        this.debtPaymentRepository = debtPaymentRepository;
        this.debtPaymentMapper = debtPaymentMapper;
    }

    @Override
    public DebtPaymentDTO save(DebtPaymentDTO debtPaymentDTO) {
        LOG.debug("Request to save DebtPayment : {}", debtPaymentDTO);
        DebtPayment debtPayment = debtPaymentMapper.toEntity(debtPaymentDTO);
        debtPayment = debtPaymentRepository.save(debtPayment);
        return debtPaymentMapper.toDto(debtPayment);
    }

    @Override
    public DebtPaymentDTO update(DebtPaymentDTO debtPaymentDTO) {
        LOG.debug("Request to update DebtPayment : {}", debtPaymentDTO);
        DebtPayment debtPayment = debtPaymentMapper.toEntity(debtPaymentDTO);
        debtPayment = debtPaymentRepository.save(debtPayment);
        return debtPaymentMapper.toDto(debtPayment);
    }

    @Override
    public Optional<DebtPaymentDTO> partialUpdate(DebtPaymentDTO debtPaymentDTO) {
        LOG.debug("Request to partially update DebtPayment : {}", debtPaymentDTO);

        return debtPaymentRepository
            .findById(debtPaymentDTO.getId())
            .map(existingDebtPayment -> {
                debtPaymentMapper.partialUpdate(existingDebtPayment, debtPaymentDTO);

                return existingDebtPayment;
            })
            .map(debtPaymentRepository::save)
            .map(debtPaymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DebtPaymentDTO> findOne(Long id) {
        LOG.debug("Request to get DebtPayment : {}", id);
        return debtPaymentRepository.findById(id).map(debtPaymentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DebtPayment : {}", id);
        debtPaymentRepository.deleteById(id);
    }
}
