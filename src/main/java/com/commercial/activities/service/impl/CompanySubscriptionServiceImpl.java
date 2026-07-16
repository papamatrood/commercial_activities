package com.commercial.activities.service.impl;

import com.commercial.activities.domain.CompanySubscription;
import com.commercial.activities.repository.CompanySubscriptionRepository;
import com.commercial.activities.service.CompanySubscriptionService;
import com.commercial.activities.service.dto.CompanySubscriptionDTO;
import com.commercial.activities.service.mapper.CompanySubscriptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.commercial.activities.domain.CompanySubscription}.
 */
@Service
@Transactional
public class CompanySubscriptionServiceImpl implements CompanySubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(CompanySubscriptionServiceImpl.class);

    private final CompanySubscriptionRepository companySubscriptionRepository;

    private final CompanySubscriptionMapper companySubscriptionMapper;

    public CompanySubscriptionServiceImpl(
        CompanySubscriptionRepository companySubscriptionRepository,
        CompanySubscriptionMapper companySubscriptionMapper
    ) {
        this.companySubscriptionRepository = companySubscriptionRepository;
        this.companySubscriptionMapper = companySubscriptionMapper;
    }

    @Override
    public CompanySubscriptionDTO save(CompanySubscriptionDTO companySubscriptionDTO) {
        LOG.debug("Request to save CompanySubscription : {}", companySubscriptionDTO);
        CompanySubscription companySubscription = companySubscriptionMapper.toEntity(companySubscriptionDTO);
        companySubscription = companySubscriptionRepository.save(companySubscription);
        return companySubscriptionMapper.toDto(companySubscription);
    }

    @Override
    public CompanySubscriptionDTO update(CompanySubscriptionDTO companySubscriptionDTO) {
        LOG.debug("Request to update CompanySubscription : {}", companySubscriptionDTO);
        CompanySubscription companySubscription = companySubscriptionMapper.toEntity(companySubscriptionDTO);
        companySubscription = companySubscriptionRepository.save(companySubscription);
        return companySubscriptionMapper.toDto(companySubscription);
    }

    @Override
    public Optional<CompanySubscriptionDTO> partialUpdate(CompanySubscriptionDTO companySubscriptionDTO) {
        LOG.debug("Request to partially update CompanySubscription : {}", companySubscriptionDTO);

        return companySubscriptionRepository
            .findById(companySubscriptionDTO.getId())
            .map(existingCompanySubscription -> {
                companySubscriptionMapper.partialUpdate(existingCompanySubscription, companySubscriptionDTO);

                return existingCompanySubscription;
            })
            .map(companySubscriptionRepository::save)
            .map(companySubscriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanySubscriptionDTO> findOne(Long id) {
        LOG.debug("Request to get CompanySubscription : {}", id);
        return companySubscriptionRepository.findById(id).map(companySubscriptionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CompanySubscription : {}", id);
        companySubscriptionRepository.deleteById(id);
    }
}
