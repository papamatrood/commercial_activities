package com.commercial.activities.service.impl;

import com.commercial.activities.domain.Supplier;
import com.commercial.activities.repository.SupplierRepository;
import com.commercial.activities.service.SupplierService;
import com.commercial.activities.service.dto.SupplierDTO;
import com.commercial.activities.service.mapper.SupplierMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.commercial.activities.domain.Supplier}.
 */
@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private static final Logger LOG = LoggerFactory.getLogger(SupplierServiceImpl.class);

    private final SupplierRepository supplierRepository;

    private final SupplierMapper supplierMapper;

    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Override
    public SupplierDTO save(SupplierDTO supplierDTO) {
        LOG.debug("Request to save Supplier : {}", supplierDTO);
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(supplier);
    }

    @Override
    public SupplierDTO update(SupplierDTO supplierDTO) {
        LOG.debug("Request to update Supplier : {}", supplierDTO);
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(supplier);
    }

    @Override
    public Optional<SupplierDTO> partialUpdate(SupplierDTO supplierDTO) {
        LOG.debug("Request to partially update Supplier : {}", supplierDTO);

        return supplierRepository
            .findById(supplierDTO.getId())
            .map(existingSupplier -> {
                supplierMapper.partialUpdate(existingSupplier, supplierDTO);

                return existingSupplier;
            })
            .map(supplierRepository::save)
            .map(supplierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierDTO> findOne(Long id) {
        LOG.debug("Request to get Supplier : {}", id);
        return supplierRepository.findById(id).map(supplierMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Supplier : {}", id);
        supplierRepository.deleteById(id);
    }
}
