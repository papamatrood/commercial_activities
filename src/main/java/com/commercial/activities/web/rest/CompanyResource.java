package com.commercial.activities.web.rest;

import com.commercial.activities.repository.CompanyRepository;
import com.commercial.activities.service.CompanyQueryService;
import com.commercial.activities.service.CompanyService;
import com.commercial.activities.service.criteria.CompanyCriteria;
import com.commercial.activities.service.dto.CompanyDTO;
import com.commercial.activities.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.commercial.activities.domain.Company}.
 */
@RestController
@RequestMapping("/api/companies")
public class CompanyResource {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyResource.class);

    private static final String ENTITY_NAME = "company";

    @Value("${jhipster.clientApp.name:commercialActivities}")
    private String applicationName;

    private final CompanyService companyService;

    private final CompanyRepository companyRepository;

    private final CompanyQueryService companyQueryService;

    public CompanyResource(CompanyService companyService, CompanyRepository companyRepository, CompanyQueryService companyQueryService) {
        this.companyService = companyService;
        this.companyRepository = companyRepository;
        this.companyQueryService = companyQueryService;
    }

    /**
     * {@code POST  /companies} : Create a new company.
     *
     * @param companyDTO the companyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new companyDTO, or with status {@code 400 (Bad Request)} if the company has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO) throws URISyntaxException {
        LOG.debug("REST request to save Company : {}", companyDTO);
        if (companyDTO.getId() != null) {
            throw new BadRequestAlertException("A new company cannot already have an ID", ENTITY_NAME, "idexists");
        }
        companyDTO = companyService.save(companyDTO);
        return ResponseEntity.created(new URI("/api/companies/" + companyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, companyDTO.getId().toString()))
            .body(companyDTO);
    }

    /**
     * {@code PUT  /companies/:id} : Updates an existing company.
     *
     * @param id the id of the companyDTO to save.
     * @param companyDTO the companyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated companyDTO,
     * or with status {@code 400 (Bad Request)} if the companyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the companyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CompanyDTO companyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Company : {}, {}", id, companyDTO);
        if (companyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, companyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!companyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        companyDTO = companyService.update(companyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyDTO.getId().toString()))
            .body(companyDTO);
    }

    /**
     * {@code PATCH  /companies/:id} : Partial updates given fields of an existing company, field will ignore if it is null
     *
     * @param id the id of the companyDTO to save.
     * @param companyDTO the companyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated companyDTO,
     * or with status {@code 400 (Bad Request)} if the companyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the companyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the companyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CompanyDTO> partialUpdateCompany(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CompanyDTO companyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Company partially : {}, {}", id, companyDTO);
        if (companyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, companyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!companyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CompanyDTO> result = companyService.partialUpdate(companyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /companies} : get all the Companies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Companies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies(CompanyCriteria criteria) {
        LOG.debug("REST request to get Companies by criteria: {}", criteria);

        List<CompanyDTO> entityList = companyQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /companies/count} : count all the companies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCompanies(CompanyCriteria criteria) {
        LOG.debug("REST request to count Companies by criteria: {}", criteria);
        return ResponseEntity.ok().body(companyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /companies/:id} : get the "id" company.
     *
     * @param id the id of the companyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the companyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Company : {}", id);
        Optional<CompanyDTO> companyDTO = companyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(companyDTO);
    }

    /**
     * {@code DELETE  /companies/:id} : delete the "id" company.
     *
     * @param id the id of the companyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Company : {}", id);
        companyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
