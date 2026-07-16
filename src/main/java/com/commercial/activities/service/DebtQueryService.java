package com.commercial.activities.service;

import com.commercial.activities.domain.*; // for static metamodels
import com.commercial.activities.domain.Debt;
import com.commercial.activities.repository.DebtRepository;
import com.commercial.activities.service.criteria.DebtCriteria;
import com.commercial.activities.service.dto.DebtDTO;
import com.commercial.activities.service.mapper.DebtMapper;
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
 * Service for executing complex queries for {@link Debt} entities in the database.
 * The main input is a {@link DebtCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DebtDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DebtQueryService extends QueryService<Debt> {

    private static final Logger LOG = LoggerFactory.getLogger(DebtQueryService.class);

    private final DebtRepository debtRepository;

    private final DebtMapper debtMapper;

    public DebtQueryService(DebtRepository debtRepository, DebtMapper debtMapper) {
        this.debtRepository = debtRepository;
        this.debtMapper = debtMapper;
    }

    /**
     * Return a {@link Page} of {@link DebtDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DebtDTO> findByCriteria(DebtCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Debt> specification = createSpecification(criteria);
        return debtRepository.findAll(specification, page).map(debtMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DebtCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Debt> specification = createSpecification(criteria);
        return debtRepository.count(specification);
    }

    /**
     * Function to convert {@link DebtCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Debt> createSpecification(DebtCriteria criteria) {
        Specification<Debt> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Debt_.company, JoinType.LEFT);
                root.fetch(Debt_.sale, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Debt_.id),
                    buildRangeSpecification(criteria.getTotalAmount(), Debt_.totalAmount),
                    buildRangeSpecification(criteria.getAmountPaid(), Debt_.amountPaid),
                    buildRangeSpecification(criteria.getRemainingAmount(), Debt_.remainingAmount),
                    buildRangeSpecification(criteria.getDate(), Debt_.date),
                    buildSpecification(criteria.getCompanyId(), root -> root.join(Debt_.company, JoinType.LEFT).get(Company_.id)),
                    buildSpecification(criteria.getSaleId(), root -> root.join(Debt_.sale, JoinType.LEFT).get(Sale_.id))
                )
            );
        }
        return specification;
    }
}
