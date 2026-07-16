package com.commercial.activities.service;

import com.commercial.activities.domain.*; // for static metamodels
import com.commercial.activities.domain.SaleLine;
import com.commercial.activities.repository.SaleLineRepository;
import com.commercial.activities.service.criteria.SaleLineCriteria;
import com.commercial.activities.service.dto.SaleLineDTO;
import com.commercial.activities.service.mapper.SaleLineMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SaleLine} entities in the database.
 * The main input is a {@link SaleLineCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SaleLineDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SaleLineQueryService extends QueryService<SaleLine> {

    private static final Logger LOG = LoggerFactory.getLogger(SaleLineQueryService.class);

    private final SaleLineRepository saleLineRepository;

    private final SaleLineMapper saleLineMapper;

    public SaleLineQueryService(SaleLineRepository saleLineRepository, SaleLineMapper saleLineMapper) {
        this.saleLineRepository = saleLineRepository;
        this.saleLineMapper = saleLineMapper;
    }

    /**
     * Return a {@link Page} of {@link SaleLineDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleLineDTO> findByCriteria(SaleLineCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SaleLine> specification = createSpecification(criteria);
        return saleLineRepository.findAll(specification, page).map(saleLineMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SaleLineCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SaleLine> specification = createSpecification(criteria);
        return saleLineRepository.count(specification);
    }

    /**
     * Function to convert {@link SaleLineCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SaleLine> createSpecification(SaleLineCriteria criteria) {
        Specification<SaleLine> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(SaleLine_.sale, JoinType.LEFT);
                root.fetch(SaleLine_.product, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), SaleLine_.id),
                    buildStringSpecification(criteria.getBarcode(), SaleLine_.barcode),
                    buildRangeSpecification(criteria.getQuantity(), SaleLine_.quantity),
                    buildRangeSpecification(criteria.getUnitPrice(), SaleLine_.unitPrice),
                    buildRangeSpecification(criteria.getTotalPrice(), SaleLine_.totalPrice),
                    buildSpecification(criteria.getSaleId(), root -> root.join(SaleLine_.sale, JoinType.LEFT).get(Sale_.id)),
                    buildSpecification(criteria.getProductId(), root -> root.join(SaleLine_.product, JoinType.LEFT).get(Product_.id))
                )
            );
        }
        return specification;
    }
}
