package com.commercial.activities.service;

import com.commercial.activities.domain.*; // for static metamodels
import com.commercial.activities.domain.Permission;
import com.commercial.activities.repository.PermissionRepository;
import com.commercial.activities.service.criteria.PermissionCriteria;
import com.commercial.activities.service.dto.PermissionDTO;
import com.commercial.activities.service.mapper.PermissionMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Permission} entities in the database.
 * The main input is a {@link PermissionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PermissionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PermissionQueryService extends QueryService<Permission> {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionQueryService.class);

    private final PermissionRepository permissionRepository;

    private final PermissionMapper permissionMapper;

    public PermissionQueryService(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    /**
     * Return a {@link List} of {@link PermissionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PermissionDTO> findByCriteria(PermissionCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Permission> specification = createSpecification(criteria);
        return permissionMapper.toDto(permissionRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PermissionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Permission> specification = createSpecification(criteria);
        return permissionRepository.count(specification);
    }

    /**
     * Function to convert {@link PermissionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Permission> createSpecification(PermissionCriteria criteria) {
        Specification<Permission> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Permission_.id),
                    buildStringSpecification(criteria.getCode(), Permission_.code),
                    buildStringSpecification(criteria.getLabel(), Permission_.label),
                    buildSpecification(criteria.getAppUserId(), root -> root.join(Permission_.appUsers, JoinType.LEFT).get(AppUser_.id))
                )
            );
        }
        return specification;
    }
}
