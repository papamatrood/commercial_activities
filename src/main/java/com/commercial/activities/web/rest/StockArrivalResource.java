package com.commercial.activities.web.rest;

import com.commercial.activities.repository.StockArrivalRepository;
import com.commercial.activities.service.StockArrivalQueryService;
import com.commercial.activities.service.StockArrivalService;
import com.commercial.activities.service.criteria.StockArrivalCriteria;
import com.commercial.activities.service.dto.StockArrivalDTO;
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
 * REST controller for managing {@link com.commercial.activities.domain.StockArrival}.
 */
@RestController
@RequestMapping("/api/stock-arrivals")
public class StockArrivalResource {

    private static final Logger LOG = LoggerFactory.getLogger(StockArrivalResource.class);

    private static final String ENTITY_NAME = "stockArrival";

    @Value("${jhipster.clientApp.name:commercialActivities}")
    private String applicationName;

    private final StockArrivalService stockArrivalService;

    private final StockArrivalRepository stockArrivalRepository;

    private final StockArrivalQueryService stockArrivalQueryService;

    public StockArrivalResource(
        StockArrivalService stockArrivalService,
        StockArrivalRepository stockArrivalRepository,
        StockArrivalQueryService stockArrivalQueryService
    ) {
        this.stockArrivalService = stockArrivalService;
        this.stockArrivalRepository = stockArrivalRepository;
        this.stockArrivalQueryService = stockArrivalQueryService;
    }

    /**
     * {@code POST  /stock-arrivals} : Create a new stockArrival.
     *
     * @param stockArrivalDTO the stockArrivalDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockArrivalDTO, or with status {@code 400 (Bad Request)} if the stockArrival has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StockArrivalDTO> createStockArrival(@Valid @RequestBody StockArrivalDTO stockArrivalDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StockArrival : {}", stockArrivalDTO);
        if (stockArrivalDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockArrival cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stockArrivalDTO = stockArrivalService.save(stockArrivalDTO);
        return ResponseEntity.created(new URI("/api/stock-arrivals/" + stockArrivalDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, stockArrivalDTO.getId().toString()))
            .body(stockArrivalDTO);
    }

    /**
     * {@code PUT  /stock-arrivals/:id} : Updates an existing stockArrival.
     *
     * @param id the id of the stockArrivalDTO to save.
     * @param stockArrivalDTO the stockArrivalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockArrivalDTO,
     * or with status {@code 400 (Bad Request)} if the stockArrivalDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockArrivalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockArrivalDTO> updateStockArrival(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockArrivalDTO stockArrivalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StockArrival : {}, {}", id, stockArrivalDTO);
        if (stockArrivalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockArrivalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockArrivalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stockArrivalDTO = stockArrivalService.update(stockArrivalDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockArrivalDTO.getId().toString()))
            .body(stockArrivalDTO);
    }

    /**
     * {@code PATCH  /stock-arrivals/:id} : Partial updates given fields of an existing stockArrival, field will ignore if it is null
     *
     * @param id the id of the stockArrivalDTO to save.
     * @param stockArrivalDTO the stockArrivalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockArrivalDTO,
     * or with status {@code 400 (Bad Request)} if the stockArrivalDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockArrivalDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockArrivalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockArrivalDTO> partialUpdateStockArrival(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockArrivalDTO stockArrivalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StockArrival partially : {}, {}", id, stockArrivalDTO);
        if (stockArrivalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockArrivalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockArrivalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockArrivalDTO> result = stockArrivalService.partialUpdate(stockArrivalDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockArrivalDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-arrivals} : get all the Stock Arrivals.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Stock Arrivals in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StockArrivalDTO>> getAllStockArrivals(
        StockArrivalCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StockArrivals by criteria: {}", criteria);

        Page<StockArrivalDTO> page = stockArrivalQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-arrivals/count} : count all the stockArrivals.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStockArrivals(StockArrivalCriteria criteria) {
        LOG.debug("REST request to count StockArrivals by criteria: {}", criteria);
        return ResponseEntity.ok().body(stockArrivalQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stock-arrivals/:id} : get the "id" stockArrival.
     *
     * @param id the id of the stockArrivalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockArrivalDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockArrivalDTO> getStockArrival(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockArrival : {}", id);
        Optional<StockArrivalDTO> stockArrivalDTO = stockArrivalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockArrivalDTO);
    }

    /**
     * {@code DELETE  /stock-arrivals/:id} : delete the "id" stockArrival.
     *
     * @param id the id of the stockArrivalDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockArrival(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StockArrival : {}", id);
        stockArrivalService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
