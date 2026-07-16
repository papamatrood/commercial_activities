package com.commercial.activities.web.rest;

import com.commercial.activities.repository.CashDisbursementRepository;
import com.commercial.activities.service.CashDisbursementQueryService;
import com.commercial.activities.service.CashDisbursementService;
import com.commercial.activities.service.criteria.CashDisbursementCriteria;
import com.commercial.activities.service.dto.CashDisbursementDTO;
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
 * REST controller for managing {@link com.commercial.activities.domain.CashDisbursement}.
 */
@RestController
@RequestMapping("/api/cash-disbursements")
public class CashDisbursementResource {

    private static final Logger LOG = LoggerFactory.getLogger(CashDisbursementResource.class);

    private static final String ENTITY_NAME = "cashDisbursement";

    @Value("${jhipster.clientApp.name:commercialActivities}")
    private String applicationName;

    private final CashDisbursementService cashDisbursementService;

    private final CashDisbursementRepository cashDisbursementRepository;

    private final CashDisbursementQueryService cashDisbursementQueryService;

    public CashDisbursementResource(
        CashDisbursementService cashDisbursementService,
        CashDisbursementRepository cashDisbursementRepository,
        CashDisbursementQueryService cashDisbursementQueryService
    ) {
        this.cashDisbursementService = cashDisbursementService;
        this.cashDisbursementRepository = cashDisbursementRepository;
        this.cashDisbursementQueryService = cashDisbursementQueryService;
    }

    /**
     * {@code POST  /cash-disbursements} : Create a new cashDisbursement.
     *
     * @param cashDisbursementDTO the cashDisbursementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cashDisbursementDTO, or with status {@code 400 (Bad Request)} if the cashDisbursement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CashDisbursementDTO> createCashDisbursement(@Valid @RequestBody CashDisbursementDTO cashDisbursementDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CashDisbursement : {}", cashDisbursementDTO);
        if (cashDisbursementDTO.getId() != null) {
            throw new BadRequestAlertException("A new cashDisbursement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cashDisbursementDTO = cashDisbursementService.save(cashDisbursementDTO);
        return ResponseEntity.created(new URI("/api/cash-disbursements/" + cashDisbursementDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cashDisbursementDTO.getId().toString()))
            .body(cashDisbursementDTO);
    }

    /**
     * {@code PUT  /cash-disbursements/:id} : Updates an existing cashDisbursement.
     *
     * @param id the id of the cashDisbursementDTO to save.
     * @param cashDisbursementDTO the cashDisbursementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cashDisbursementDTO,
     * or with status {@code 400 (Bad Request)} if the cashDisbursementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cashDisbursementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CashDisbursementDTO> updateCashDisbursement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CashDisbursementDTO cashDisbursementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CashDisbursement : {}, {}", id, cashDisbursementDTO);
        if (cashDisbursementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cashDisbursementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cashDisbursementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cashDisbursementDTO = cashDisbursementService.update(cashDisbursementDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cashDisbursementDTO.getId().toString()))
            .body(cashDisbursementDTO);
    }

    /**
     * {@code PATCH  /cash-disbursements/:id} : Partial updates given fields of an existing cashDisbursement, field will ignore if it is null
     *
     * @param id the id of the cashDisbursementDTO to save.
     * @param cashDisbursementDTO the cashDisbursementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cashDisbursementDTO,
     * or with status {@code 400 (Bad Request)} if the cashDisbursementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cashDisbursementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cashDisbursementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CashDisbursementDTO> partialUpdateCashDisbursement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CashDisbursementDTO cashDisbursementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CashDisbursement partially : {}, {}", id, cashDisbursementDTO);
        if (cashDisbursementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cashDisbursementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cashDisbursementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CashDisbursementDTO> result = cashDisbursementService.partialUpdate(cashDisbursementDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cashDisbursementDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cash-disbursements} : get all the Cash Disbursements.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Cash Disbursements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CashDisbursementDTO>> getAllCashDisbursements(
        CashDisbursementCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CashDisbursements by criteria: {}", criteria);

        Page<CashDisbursementDTO> page = cashDisbursementQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cash-disbursements/count} : count all the cashDisbursements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCashDisbursements(CashDisbursementCriteria criteria) {
        LOG.debug("REST request to count CashDisbursements by criteria: {}", criteria);
        return ResponseEntity.ok().body(cashDisbursementQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cash-disbursements/:id} : get the "id" cashDisbursement.
     *
     * @param id the id of the cashDisbursementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cashDisbursementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CashDisbursementDTO> getCashDisbursement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CashDisbursement : {}", id);
        Optional<CashDisbursementDTO> cashDisbursementDTO = cashDisbursementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cashDisbursementDTO);
    }

    /**
     * {@code DELETE  /cash-disbursements/:id} : delete the "id" cashDisbursement.
     *
     * @param id the id of the cashDisbursementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCashDisbursement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CashDisbursement : {}", id);
        cashDisbursementService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
