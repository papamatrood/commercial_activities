package com.commercial.activities.web.rest;

import com.commercial.activities.repository.CompanySubscriptionRepository;
import com.commercial.activities.service.CompanySubscriptionQueryService;
import com.commercial.activities.service.CompanySubscriptionService;
import com.commercial.activities.service.criteria.CompanySubscriptionCriteria;
import com.commercial.activities.service.dto.CompanySubscriptionDTO;
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
 * REST controller for managing {@link com.commercial.activities.domain.CompanySubscription}.
 */
@RestController
@RequestMapping("/api/company-subscriptions")
public class CompanySubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(CompanySubscriptionResource.class);

    private static final String ENTITY_NAME = "companySubscription";

    @Value("${jhipster.clientApp.name:commercialActivities}")
    private String applicationName;

    private final CompanySubscriptionService companySubscriptionService;

    private final CompanySubscriptionRepository companySubscriptionRepository;

    private final CompanySubscriptionQueryService companySubscriptionQueryService;

    public CompanySubscriptionResource(
        CompanySubscriptionService companySubscriptionService,
        CompanySubscriptionRepository companySubscriptionRepository,
        CompanySubscriptionQueryService companySubscriptionQueryService
    ) {
        this.companySubscriptionService = companySubscriptionService;
        this.companySubscriptionRepository = companySubscriptionRepository;
        this.companySubscriptionQueryService = companySubscriptionQueryService;
    }

    /**
     * {@code POST  /company-subscriptions} : Create a new companySubscription.
     *
     * @param companySubscriptionDTO the companySubscriptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new companySubscriptionDTO, or with status {@code 400 (Bad Request)} if the companySubscription has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CompanySubscriptionDTO> createCompanySubscription(
        @Valid @RequestBody CompanySubscriptionDTO companySubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save CompanySubscription : {}", companySubscriptionDTO);
        if (companySubscriptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new companySubscription cannot already have an ID", ENTITY_NAME, "idexists");
        }
        companySubscriptionDTO = companySubscriptionService.save(companySubscriptionDTO);
        return ResponseEntity.created(new URI("/api/company-subscriptions/" + companySubscriptionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, companySubscriptionDTO.getId().toString()))
            .body(companySubscriptionDTO);
    }

    /**
     * {@code PUT  /company-subscriptions/:id} : Updates an existing companySubscription.
     *
     * @param id the id of the companySubscriptionDTO to save.
     * @param companySubscriptionDTO the companySubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated companySubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the companySubscriptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the companySubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CompanySubscriptionDTO> updateCompanySubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CompanySubscriptionDTO companySubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CompanySubscription : {}, {}", id, companySubscriptionDTO);
        if (companySubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, companySubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!companySubscriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        companySubscriptionDTO = companySubscriptionService.update(companySubscriptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companySubscriptionDTO.getId().toString()))
            .body(companySubscriptionDTO);
    }

    /**
     * {@code PATCH  /company-subscriptions/:id} : Partial updates given fields of an existing companySubscription, field will ignore if it is null
     *
     * @param id the id of the companySubscriptionDTO to save.
     * @param companySubscriptionDTO the companySubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated companySubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the companySubscriptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the companySubscriptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the companySubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CompanySubscriptionDTO> partialUpdateCompanySubscription(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CompanySubscriptionDTO companySubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CompanySubscription partially : {}, {}", id, companySubscriptionDTO);
        if (companySubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, companySubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!companySubscriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CompanySubscriptionDTO> result = companySubscriptionService.partialUpdate(companySubscriptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companySubscriptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /company-subscriptions} : get all the Company Subscriptions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Company Subscriptions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CompanySubscriptionDTO>> getAllCompanySubscriptions(CompanySubscriptionCriteria criteria) {
        LOG.debug("REST request to get CompanySubscriptions by criteria: {}", criteria);

        List<CompanySubscriptionDTO> entityList = companySubscriptionQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /company-subscriptions/count} : count all the companySubscriptions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCompanySubscriptions(CompanySubscriptionCriteria criteria) {
        LOG.debug("REST request to count CompanySubscriptions by criteria: {}", criteria);
        return ResponseEntity.ok().body(companySubscriptionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /company-subscriptions/:id} : get the "id" companySubscription.
     *
     * @param id the id of the companySubscriptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the companySubscriptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanySubscriptionDTO> getCompanySubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CompanySubscription : {}", id);
        Optional<CompanySubscriptionDTO> companySubscriptionDTO = companySubscriptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(companySubscriptionDTO);
    }

    /**
     * {@code DELETE  /company-subscriptions/:id} : delete the "id" companySubscription.
     *
     * @param id the id of the companySubscriptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompanySubscription(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CompanySubscription : {}", id);
        companySubscriptionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
