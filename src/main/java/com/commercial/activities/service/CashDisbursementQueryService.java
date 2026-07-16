package com.commercial.activities.service;

import com.commercial.activities.domain.*; // for static metamodels
import com.commercial.activities.domain.CashDisbursement;
import com.commercial.activities.repository.CashDisbursementRepository;
import com.commercial.activities.service.criteria.CashDisbursementCriteria;
import com.commercial.activities.service.dto.CashDisbursementDTO;
import com.commercial.activities.service.mapper.CashDisbursementMapper;
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
 * Service for executing complex queries for {@link CashDisbursement} entities in the database.
 * The main input is a {@link CashDisbursementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CashDisbursementDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CashDisbursementQueryService extends QueryService<CashDisbursement> {

    private static final Logger LOG = LoggerFactory.getLogger(CashDisbursementQueryService.class);

    private final CashDisbursementRepository cashDisbursementRepository;

    private final CashDisbursementMapper cashDisbursementMapper;

    public CashDisbursementQueryService(
        CashDisbursementRepository cashDisbursementRepository,
        CashDisbursementMapper cashDisbursementMapper
    ) {
        this.cashDisbursementRepository = cashDisbursementRepository;
        this.cashDisbursementMapper = cashDisbursementMapper;
    }

    /**
     * Return a {@link Page} of {@link CashDisbursementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CashDisbursementDTO> findByCriteria(CashDisbursementCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CashDisbursement> specification = createSpecification(criteria);
        return cashDisbursementRepository.findAll(specification, page).map(cashDisbursementMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CashDisbursementCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CashDisbursement> specification = createSpecification(criteria);
        return cashDisbursementRepository.count(specification);
    }

    /**
     * Function to convert {@link CashDisbursementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CashDisbursement> createSpecification(CashDisbursementCriteria criteria) {
        Specification<CashDisbursement> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CashDisbursement_.company, JoinType.LEFT);
                root.fetch(CashDisbursement_.user, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CashDisbursement_.id),
                    buildStringSpecification(criteria.getReason(), CashDisbursement_.reason),
                    buildRangeSpecification(criteria.getAmount(), CashDisbursement_.amount),
                    buildRangeSpecification(criteria.getDate(), CashDisbursement_.date),
                    buildSpecification(criteria.getCompanyId(), root ->
                        root.join(CashDisbursement_.company, JoinType.LEFT).get(Company_.id)
                    ),
                    buildSpecification(criteria.getUserId(), root -> root.join(CashDisbursement_.user, JoinType.LEFT).get(AppUser_.id))
                )
            );
        }
        return specification;
    }
}
