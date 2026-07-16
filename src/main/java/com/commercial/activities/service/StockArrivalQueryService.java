package com.commercial.activities.service;

import com.commercial.activities.domain.*; // for static metamodels
import com.commercial.activities.domain.StockArrival;
import com.commercial.activities.repository.StockArrivalRepository;
import com.commercial.activities.service.criteria.StockArrivalCriteria;
import com.commercial.activities.service.dto.StockArrivalDTO;
import com.commercial.activities.service.mapper.StockArrivalMapper;
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
 * Service for executing complex queries for {@link StockArrival} entities in the database.
 * The main input is a {@link StockArrivalCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StockArrivalDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockArrivalQueryService extends QueryService<StockArrival> {

    private static final Logger LOG = LoggerFactory.getLogger(StockArrivalQueryService.class);

    private final StockArrivalRepository stockArrivalRepository;

    private final StockArrivalMapper stockArrivalMapper;

    public StockArrivalQueryService(StockArrivalRepository stockArrivalRepository, StockArrivalMapper stockArrivalMapper) {
        this.stockArrivalRepository = stockArrivalRepository;
        this.stockArrivalMapper = stockArrivalMapper;
    }

    /**
     * Return a {@link Page} of {@link StockArrivalDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockArrivalDTO> findByCriteria(StockArrivalCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockArrival> specification = createSpecification(criteria);
        return stockArrivalRepository.findAll(specification, page).map(stockArrivalMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockArrivalCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StockArrival> specification = createSpecification(criteria);
        return stockArrivalRepository.count(specification);
    }

    /**
     * Function to convert {@link StockArrivalCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockArrival> createSpecification(StockArrivalCriteria criteria) {
        Specification<StockArrival> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(StockArrival_.product, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), StockArrival_.id),
                    buildStringSpecification(criteria.getBarcode(), StockArrival_.barcode),
                    buildRangeSpecification(criteria.getQuantity(), StockArrival_.quantity),
                    buildRangeSpecification(criteria.getAmount(), StockArrival_.amount),
                    buildRangeSpecification(criteria.getDate(), StockArrival_.date),
                    buildSpecification(criteria.getProductId(), root -> root.join(StockArrival_.product, JoinType.LEFT).get(Product_.id))
                )
            );
        }
        return specification;
    }
}
