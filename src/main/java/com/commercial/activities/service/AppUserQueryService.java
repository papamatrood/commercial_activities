package com.commercial.activities.service;

import com.commercial.activities.domain.*; // for static metamodels
import com.commercial.activities.domain.AppUser;
import com.commercial.activities.repository.AppUserRepository;
import com.commercial.activities.service.criteria.AppUserCriteria;
import com.commercial.activities.service.dto.AppUserDTO;
import com.commercial.activities.service.mapper.AppUserMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AppUser} entities in the database.
 * The main input is a {@link AppUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AppUserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AppUserQueryService extends QueryService<AppUser> {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserQueryService.class);

    private final AppUserRepository appUserRepository;

    private final AppUserMapper appUserMapper;

    public AppUserQueryService(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }

    /**
     * Return a {@link List} of {@link AppUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AppUserDTO> findByCriteria(AppUserCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<AppUser> specification = createSpecification(criteria);
        return appUserMapper.toDto(appUserRepository.fetchBagRelationships(appUserRepository.findAll(specification)));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AppUserCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AppUser> specification = createSpecification(criteria);
        return appUserRepository.count(specification);
    }

    /**
     * Function to convert {@link AppUserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AppUser> createSpecification(AppUserCriteria criteria) {
        Specification<AppUser> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(AppUser_.user, JoinType.LEFT);
                root.fetch(AppUser_.company, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), AppUser_.id),
                    buildStringSpecification(criteria.getPhoneNumber(), AppUser_.phoneNumber),
                    buildSpecification(criteria.getType(), AppUser_.type),
                    buildRangeSpecification(criteria.getBirthDate(), AppUser_.birthDate),
                    buildStringSpecification(criteria.getBirthPlace(), AppUser_.birthPlace),
                    buildSpecification(criteria.getGender(), AppUser_.gender),
                    buildSpecification(criteria.getDisabled(), AppUser_.disabled),
                    buildRangeSpecification(criteria.getDisabledDate(), AppUser_.disabledDate),
                    buildSpecification(criteria.getUserId(), root -> root.join(AppUser_.user, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getCompanyId(), root -> root.join(AppUser_.company, JoinType.LEFT).get(Company_.id)),
                    buildSpecification(criteria.getPermissionId(), root ->
                        root.join(AppUser_.permissions, JoinType.LEFT).get(Permission_.id)
                    )
                )
            );
        }
        return specification;
    }
}
