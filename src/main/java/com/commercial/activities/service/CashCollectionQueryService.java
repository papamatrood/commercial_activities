package com.commercial.activities.service;

import com.commercial.activities.domain.*; // for static metamodels
import com.commercial.activities.domain.CashCollection;
import com.commercial.activities.repository.CashCollectionRepository;
import com.commercial.activities.service.criteria.CashCollectionCriteria;
import com.commercial.activities.service.dto.CashCollectionDTO;
import com.commercial.activities.service.mapper.CashCollectionMapper;
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
 * Service for executing complex queries for {@link CashCollection} entities in the database.
 * The main input is a {@link CashCollectionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CashCollectionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CashCollectionQueryService extends QueryService<CashCollection> {

    private static final Logger LOG = LoggerFactory.getLogger(CashCollectionQueryService.class);

    private final CashCollectionRepository cashCollectionRepository;

    private final CashCollectionMapper cashCollectionMapper;

    public CashCollectionQueryService(CashCollectionRepository cashCollectionRepository, CashCollectionMapper cashCollectionMapper) {
        this.cashCollectionRepository = cashCollectionRepository;
        this.cashCollectionMapper = cashCollectionMapper;
    }

    /**
     * Return a {@link Page} of {@link CashCollectionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CashCollectionDTO> findByCriteria(CashCollectionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CashCollection> specification = createSpecification(criteria);
        return cashCollectionRepository.findAll(specification, page).map(cashCollectionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CashCollectionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CashCollection> specification = createSpecification(criteria);
        return cashCollectionRepository.count(specification);
    }

    /**
     * Function to convert {@link CashCollectionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CashCollection> createSpecification(CashCollectionCriteria criteria) {
        Specification<CashCollection> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CashCollection_.company, JoinType.LEFT);
                root.fetch(CashCollection_.user, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CashCollection_.id),
                    buildRangeSpecification(criteria.getDate(), CashCollection_.date),
                    buildRangeSpecification(criteria.getAmount(), CashCollection_.amount),
                    buildSpecification(criteria.getCompanyId(), root -> root.join(CashCollection_.company, JoinType.LEFT).get(Company_.id)),
                    buildSpecification(criteria.getUserId(), root -> root.join(CashCollection_.user, JoinType.LEFT).get(AppUser_.id))
                )
            );
        }
        return specification;
    }
}
