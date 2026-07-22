package com.commercial.activities.service;

import com.commercial.activities.service.dto.CompanyWithOwnerDTO;
import java.util.Optional;

/**
 * Service Interface for managing a {@link com.commercial.activities.domain.Company} together with
 * its owner ({@link com.commercial.activities.domain.User} + {@link com.commercial.activities.domain.AppUser}).
 */
public interface CompanyOwnerService {
    /**
     * Create a new company together with a new owner account.
     *
     * @param companyWithOwnerDTO the company and owner to create.
     * @return the persisted company and owner.
     */
    CompanyWithOwnerDTO create(CompanyWithOwnerDTO companyWithOwnerDTO);

    /**
     * Update an existing company and its owner (creating the owner if none exists yet).
     *
     * @param companyId the id of the company to update.
     * @param companyWithOwnerDTO the company and owner data to save.
     * @return the persisted company and owner.
     */
    CompanyWithOwnerDTO update(Long companyId, CompanyWithOwnerDTO companyWithOwnerDTO);

    /**
     * Get the "id" company together with its owner (the first {@code COMPANY_ADMIN} {@code AppUser} linked to it).
     *
     * @param companyId the id of the company.
     * @return the company and its owner, if the company exists.
     */
    Optional<CompanyWithOwnerDTO> findOneWithOwner(Long companyId);
}
