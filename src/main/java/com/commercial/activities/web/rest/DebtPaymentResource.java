package com.commercial.activities.web.rest;

import com.commercial.activities.repository.DebtPaymentRepository;
import com.commercial.activities.service.DebtPaymentQueryService;
import com.commercial.activities.service.DebtPaymentService;
import com.commercial.activities.service.criteria.DebtPaymentCriteria;
import com.commercial.activities.service.dto.DebtPaymentDTO;
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
 * REST controller for managing {@link com.commercial.activities.domain.DebtPayment}.
 */
@RestController
@RequestMapping("/api/debt-payments")
public class DebtPaymentResource {

    private static final Logger LOG = LoggerFactory.getLogger(DebtPaymentResource.class);

    private static final String ENTITY_NAME = "debtPayment";

    @Value("${jhipster.clientApp.name:commercialActivities}")
    private String applicationName;

    private final DebtPaymentService debtPaymentService;

    private final DebtPaymentRepository debtPaymentRepository;

    private final DebtPaymentQueryService debtPaymentQueryService;

    public DebtPaymentResource(
        DebtPaymentService debtPaymentService,
        DebtPaymentRepository debtPaymentRepository,
        DebtPaymentQueryService debtPaymentQueryService
    ) {
        this.debtPaymentService = debtPaymentService;
        this.debtPaymentRepository = debtPaymentRepository;
        this.debtPaymentQueryService = debtPaymentQueryService;
    }

    /**
     * {@code POST  /debt-payments} : Create a new debtPayment.
     *
     * @param debtPaymentDTO the debtPaymentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new debtPaymentDTO, or with status {@code 400 (Bad Request)} if the debtPayment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DebtPaymentDTO> createDebtPayment(@Valid @RequestBody DebtPaymentDTO debtPaymentDTO) throws URISyntaxException {
        LOG.debug("REST request to save DebtPayment : {}", debtPaymentDTO);
        if (debtPaymentDTO.getId() != null) {
            throw new BadRequestAlertException("A new debtPayment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        debtPaymentDTO = debtPaymentService.save(debtPaymentDTO);
        return ResponseEntity.created(new URI("/api/debt-payments/" + debtPaymentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, debtPaymentDTO.getId().toString()))
            .body(debtPaymentDTO);
    }

    /**
     * {@code PUT  /debt-payments/:id} : Updates an existing debtPayment.
     *
     * @param id the id of the debtPaymentDTO to save.
     * @param debtPaymentDTO the debtPaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated debtPaymentDTO,
     * or with status {@code 400 (Bad Request)} if the debtPaymentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the debtPaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DebtPaymentDTO> updateDebtPayment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DebtPaymentDTO debtPaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DebtPayment : {}, {}", id, debtPaymentDTO);
        if (debtPaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, debtPaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!debtPaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        debtPaymentDTO = debtPaymentService.update(debtPaymentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, debtPaymentDTO.getId().toString()))
            .body(debtPaymentDTO);
    }

    /**
     * {@code PATCH  /debt-payments/:id} : Partial updates given fields of an existing debtPayment, field will ignore if it is null
     *
     * @param id the id of the debtPaymentDTO to save.
     * @param debtPaymentDTO the debtPaymentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated debtPaymentDTO,
     * or with status {@code 400 (Bad Request)} if the debtPaymentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the debtPaymentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the debtPaymentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DebtPaymentDTO> partialUpdateDebtPayment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DebtPaymentDTO debtPaymentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DebtPayment partially : {}, {}", id, debtPaymentDTO);
        if (debtPaymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, debtPaymentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!debtPaymentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DebtPaymentDTO> result = debtPaymentService.partialUpdate(debtPaymentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, debtPaymentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /debt-payments} : get all the Debt Payments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Debt Payments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DebtPaymentDTO>> getAllDebtPayments(
        DebtPaymentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DebtPayments by criteria: {}", criteria);

        Page<DebtPaymentDTO> page = debtPaymentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /debt-payments/count} : count all the debtPayments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDebtPayments(DebtPaymentCriteria criteria) {
        LOG.debug("REST request to count DebtPayments by criteria: {}", criteria);
        return ResponseEntity.ok().body(debtPaymentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /debt-payments/:id} : get the "id" debtPayment.
     *
     * @param id the id of the debtPaymentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the debtPaymentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DebtPaymentDTO> getDebtPayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DebtPayment : {}", id);
        Optional<DebtPaymentDTO> debtPaymentDTO = debtPaymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(debtPaymentDTO);
    }

    /**
     * {@code DELETE  /debt-payments/:id} : delete the "id" debtPayment.
     *
     * @param id the id of the debtPaymentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDebtPayment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DebtPayment : {}", id);
        debtPaymentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
