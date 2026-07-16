package com.commercial.activities.service;

import com.commercial.activities.domain.*; // for static metamodels
import com.commercial.activities.domain.DebtPayment;
import com.commercial.activities.repository.DebtPaymentRepository;
import com.commercial.activities.service.criteria.DebtPaymentCriteria;
import com.commercial.activities.service.dto.DebtPaymentDTO;
import com.commercial.activities.service.mapper.DebtPaymentMapper;
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
 * Service for executing complex queries for {@link DebtPayment} entities in the database.
 * The main input is a {@link DebtPaymentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DebtPaymentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DebtPaymentQueryService extends QueryService<DebtPayment> {

    private static final Logger LOG = LoggerFactory.getLogger(DebtPaymentQueryService.class);

    private final DebtPaymentRepository debtPaymentRepository;

    private final DebtPaymentMapper debtPaymentMapper;

    public DebtPaymentQueryService(DebtPaymentRepository debtPaymentRepository, DebtPaymentMapper debtPaymentMapper) {
        this.debtPaymentRepository = debtPaymentRepository;
        this.debtPaymentMapper = debtPaymentMapper;
    }

    /**
     * Return a {@link Page} of {@link DebtPaymentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DebtPaymentDTO> findByCriteria(DebtPaymentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DebtPayment> specification = createSpecification(criteria);
        return debtPaymentRepository.findAll(specification, page).map(debtPaymentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DebtPaymentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DebtPayment> specification = createSpecification(criteria);
        return debtPaymentRepository.count(specification);
    }

    /**
     * Function to convert {@link DebtPaymentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DebtPayment> createSpecification(DebtPaymentCriteria criteria) {
        Specification<DebtPayment> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(DebtPayment_.debt, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), DebtPayment_.id),
                    buildRangeSpecification(criteria.getAmountPaid(), DebtPayment_.amountPaid),
                    buildRangeSpecification(criteria.getRemainingAmount(), DebtPayment_.remainingAmount),
                    buildRangeSpecification(criteria.getDate(), DebtPayment_.date),
                    buildSpecification(criteria.getDebtId(), root -> root.join(DebtPayment_.debt, JoinType.LEFT).get(Debt_.id))
                )
            );
        }
        return specification;
    }
}
