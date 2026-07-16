package com.commercial.activities.web.rest;

import com.commercial.activities.repository.CashCollectionRepository;
import com.commercial.activities.service.CashCollectionQueryService;
import com.commercial.activities.service.CashCollectionService;
import com.commercial.activities.service.criteria.CashCollectionCriteria;
import com.commercial.activities.service.dto.CashCollectionDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.commercial.activities.domain.CashCollection}.
 */
@RestController
@RequestMapping("/api/cash-collections")
public class CashCollectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(CashCollectionResource.class);

    private static final String ENTITY_NAME = "cashCollection";

    @Value("${jhipster.clientApp.name:commercialActivities}")
    private String applicationName;

    private final CashCollectionService cashCollectionService;

    private final CashCollectionRepository cashCollectionRepository;

    private final CashCollectionQueryService cashCollectionQueryService;

    public CashCollectionResource(
        CashCollectionService cashCollectionService,
        CashCollectionRepository cashCollectionRepository,
        CashCollectionQueryService cashCollectionQueryService
    ) {
        this.cashCollectionService = cashCollectionService;
        this.cashCollectionRepository = cashCollectionRepository;
        this.cashCollectionQueryService = cashCollectionQueryService;
    }

    /**
     * {@code POST  /cash-collections} : Create a new cashCollection.
     *
     * @param cashCollectionDTO the cashCollectionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cashCollectionDTO, or with status {@code 400 (Bad Request)} if the cashCollection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CashCollectionDTO> createCashCollection(@Valid @RequestBody CashCollectionDTO cashCollectionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CashCollection : {}", cashCollectionDTO);
        if (cashCollectionDTO.getId() != null) {
            throw new BadRequestAlertException("A new cashCollection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cashCollectionDTO = cashCollectionService.save(cashCollectionDTO);
        return ResponseEntity.created(new URI("/api/cash-collections/" + cashCollectionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cashCollectionDTO.getId().toString()))
            .body(cashCollectionDTO);
    }

    /**
     * {@code PUT  /cash-collections/:id} : Updates an existing cashCollection.
     *
     * @param id the id of the cashCollectionDTO to save.
     * @param cashCollectionDTO the cashCollectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cashCollectionDTO,
     * or with status {@code 400 (Bad Request)} if the cashCollectionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cashCollectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CashCollectionDTO> updateCashCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CashCollectionDTO cashCollectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CashCollection : {}, {}", id, cashCollectionDTO);
        if (cashCollectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cashCollectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cashCollectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cashCollectionDTO = cashCollectionService.update(cashCollectionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cashCollectionDTO.getId().toString()))
            .body(cashCollectionDTO);
    }

    /**
     * {@code PATCH  /cash-collections/:id} : Partial updates given fields of an existing cashCollection, field will ignore if it is null
     *
     * @param id the id of the cashCollectionDTO to save.
     * @param cashCollectionDTO the cashCollectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cashCollectionDTO,
     * or with status {@code 400 (Bad Request)} if the cashCollectionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cashCollectionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cashCollectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CashCollectionDTO> partialUpdateCashCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CashCollectionDTO cashCollectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CashCollection partially : {}, {}", id, cashCollectionDTO);
        if (cashCollectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cashCollectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cashCollectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CashCollectionDTO> result = cashCollectionService.partialUpdate(cashCollectionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cashCollectionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cash-collections} : get all the Cash Collections.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Cash Collections in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CashCollectionDTO>> getAllCashCollections(
        CashCollectionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CashCollections by criteria: {}", criteria);

        Page<CashCollectionDTO> page = cashCollectionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cash-collections/count} : count all the cashCollections.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCashCollections(CashCollectionCriteria criteria) {
        LOG.debug("REST request to count CashCollections by criteria: {}", criteria);
        return ResponseEntity.ok().body(cashCollectionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cash-collections/:id} : get the "id" cashCollection.
     *
     * @param id the id of the cashCollectionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cashCollectionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CashCollectionDTO> getCashCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CashCollection : {}", id);
        Optional<CashCollectionDTO> cashCollectionDTO = cashCollectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cashCollectionDTO);
    }

    /**
     * {@code DELETE  /cash-collections/:id} : delete the "id" cashCollection.
     *
     * @param id the id of the cashCollectionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCashCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CashCollection : {}", id);
        cashCollectionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
