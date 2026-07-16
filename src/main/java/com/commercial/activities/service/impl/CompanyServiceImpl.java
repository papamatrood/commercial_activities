package com.commercial.activities.service.impl;

import com.commercial.activities.domain.Company;
import com.commercial.activities.repository.CompanyRepository;
import com.commercial.activities.service.CompanyService;
import com.commercial.activities.service.dto.CompanyDTO;
import com.commercial.activities.service.mapper.CompanyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.commercial.activities.domain.Company}.
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyRepository companyRepository;

    private final CompanyMapper companyMapper;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Override
    public CompanyDTO save(CompanyDTO companyDTO) {
        LOG.debug("Request to save Company : {}", companyDTO);
        Company company = companyMapper.toEntity(companyDTO);
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }

    @Override
    public CompanyDTO update(CompanyDTO companyDTO) {
        LOG.debug("Request to update Company : {}", companyDTO);
        Company company = companyMapper.toEntity(companyDTO);
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }

    @Override
    public Optional<CompanyDTO> partialUpdate(CompanyDTO companyDTO) {
        LOG.debug("Request to partially update Company : {}", companyDTO);

        return companyRepository
            .findById(companyDTO.getId())
            .map(existingCompany -> {
                companyMapper.partialUpdate(existingCompany, companyDTO);

                return existingCompany;
            })
            .map(companyRepository::save)
            .map(companyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> findOne(Long id) {
        LOG.debug("Request to get Company : {}", id);
        return companyRepository.findById(id).map(companyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Company : {}", id);
        companyRepository.deleteById(id);
    }
}
