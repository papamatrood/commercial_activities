package com.commercial.activities.web.rest;

import com.commercial.activities.service.CompanyOwnerService;
import com.commercial.activities.service.dto.CompanyWithOwnerDTO;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for creating and editing a {@link com.commercial.activities.domain.Company}
 * together with its owner in a single request.
 */
@RestController
@RequestMapping("/api/companies")
public class CompanyOwnerResource {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyOwnerResource.class);

    private final CompanyOwnerService companyOwnerService;

    public CompanyOwnerResource(CompanyOwnerService companyOwnerService) {
        this.companyOwnerService = companyOwnerService;
    }

    /**
     * {@code POST  /companies/with-owner} : Create a new company together with a new owner.
     *
     * @param companyWithOwnerDTO the company and owner to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new company and owner.
     */
    @PostMapping("/with-owner")
    public ResponseEntity<CompanyWithOwnerDTO> createCompanyWithOwner(@Valid @RequestBody CompanyWithOwnerDTO companyWithOwnerDTO) {
        LOG.debug("REST request to save Company with owner : {}", companyWithOwnerDTO);
        CompanyWithOwnerDTO result = companyOwnerService.create(companyWithOwnerDTO);
        return ResponseEntity.created(URI.create("/api/companies/" + result.getCompany().getId() + "/with-owner")).body(result);
    }

    /**
     * {@code PUT  /companies/:id/with-owner} : Update an existing company and its owner.
     *
     * @param id the id of the company to update.
     * @param companyWithOwnerDTO the company and owner data to save.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated company and owner.
     */
    @PutMapping("/{id}/with-owner")
    public ResponseEntity<CompanyWithOwnerDTO> updateCompanyWithOwner(
        @PathVariable("id") Long id,
        @Valid @RequestBody CompanyWithOwnerDTO companyWithOwnerDTO
    ) {
        LOG.debug("REST request to update Company with owner : {}, {}", id, companyWithOwnerDTO);
        CompanyWithOwnerDTO result = companyOwnerService.update(id, companyWithOwnerDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code GET  /companies/:id/with-owner} : get the "id" company together with its owner.
     *
     * @param id the id of the company to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the company and owner, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}/with-owner")
    public ResponseEntity<CompanyWithOwnerDTO> getCompanyWithOwner(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Company with owner : {}", id);
        Optional<CompanyWithOwnerDTO> companyWithOwnerDTO = companyOwnerService.findOneWithOwner(id);
        return ResponseUtil.wrapOrNotFound(companyWithOwnerDTO);
    }
}
