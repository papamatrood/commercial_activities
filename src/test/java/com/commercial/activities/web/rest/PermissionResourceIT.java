package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.PermissionAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.Permission;
import com.commercial.activities.repository.PermissionRepository;
import com.commercial.activities.service.dto.PermissionDTO;
import com.commercial.activities.service.mapper.PermissionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PermissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PermissionResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/permissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPermissionMockMvc;

    private Permission permission;

    private Permission insertedPermission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Permission createEntity() {
        return new Permission().code(DEFAULT_CODE).label(DEFAULT_LABEL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Permission createUpdatedEntity() {
        return new Permission().code(UPDATED_CODE).label(UPDATED_LABEL);
    }

    @BeforeEach
    void initTest() {
        permission = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPermission != null) {
            permissionRepository.delete(insertedPermission);
            insertedPermission = null;
        }
    }

    @Test
    @Transactional
    void createPermission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);
        var returnedPermissionDTO = om.readValue(
            restPermissionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PermissionDTO.class
        );

        // Validate the Permission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPermission = permissionMapper.toEntity(returnedPermissionDTO);
        assertPermissionUpdatableFieldsEquals(returnedPermission, getPersistedPermission(returnedPermission));

        insertedPermission = returnedPermission;
    }

    @Test
    @Transactional
    void createPermissionWithExistingId() throws Exception {
        // Create the Permission with an existing ID
        permission.setId(1L);
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPermissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        permission.setCode(null);

        // Create the Permission, which fails.
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        restPermissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        permission.setLabel(null);

        // Create the Permission, which fails.
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        restPermissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPermissions() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList
        restPermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(permission.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)));
    }

    @Test
    @Transactional
    void getPermission() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get the permission
        restPermissionMockMvc
            .perform(get(ENTITY_API_URL_ID, permission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(permission.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL));
    }

    @Test
    @Transactional
    void getPermissionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        Long id = permission.getId();

        defaultPermissionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPermissionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPermissionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPermissionsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList where code equals to
        defaultPermissionFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPermissionsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList where code in
        defaultPermissionFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPermissionsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList where code is not null
        defaultPermissionFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllPermissionsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList where code contains
        defaultPermissionFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPermissionsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList where code does not contain
        defaultPermissionFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllPermissionsByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList where label equals to
        defaultPermissionFiltering("label.equals=" + DEFAULT_LABEL, "label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllPermissionsByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList where label in
        defaultPermissionFiltering("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL, "label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllPermissionsByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList where label is not null
        defaultPermissionFiltering("label.specified=true", "label.specified=false");
    }

    @Test
    @Transactional
    void getAllPermissionsByLabelContainsSomething() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList where label contains
        defaultPermissionFiltering("label.contains=" + DEFAULT_LABEL, "label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllPermissionsByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        // Get all the permissionList where label does not contain
        defaultPermissionFiltering("label.doesNotContain=" + UPDATED_LABEL, "label.doesNotContain=" + DEFAULT_LABEL);
    }

    @Test
    @Transactional
    void getAllPermissionsByAppUserIsEqualToSomething() throws Exception {
        AppUser appUser;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            permissionRepository.saveAndFlush(permission);
            appUser = AppUserResourceIT.createEntity();
        } else {
            appUser = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(appUser);
        em.flush();
        permission.addAppUser(appUser);
        permissionRepository.saveAndFlush(permission);
        Long appUserId = appUser.getId();
        // Get all the permissionList where appUser equals to appUserId
        defaultPermissionShouldBeFound("appUserId.equals=" + appUserId);

        // Get all the permissionList where appUser equals to (appUserId + 1)
        defaultPermissionShouldNotBeFound("appUserId.equals=" + (appUserId + 1));
    }

    private void defaultPermissionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPermissionShouldBeFound(shouldBeFound);
        defaultPermissionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPermissionShouldBeFound(String filter) throws Exception {
        restPermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(permission.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)));

        // Check, that the count call also returns 1
        restPermissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPermissionShouldNotBeFound(String filter) throws Exception {
        restPermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPermissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPermission() throws Exception {
        // Get the permission
        restPermissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPermission() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the permission
        Permission updatedPermission = permissionRepository.findById(permission.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPermission are not directly saved in db
        em.detach(updatedPermission);
        updatedPermission.code(UPDATED_CODE).label(UPDATED_LABEL);
        PermissionDTO permissionDTO = permissionMapper.toDto(updatedPermission);

        restPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, permissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(permissionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPermissionToMatchAllProperties(updatedPermission);
    }

    @Test
    @Transactional
    void putNonExistingPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, permissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(permissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(permissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePermissionWithPatch() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the permission using partial update
        Permission partialUpdatedPermission = new Permission();
        partialUpdatedPermission.setId(permission.getId());

        partialUpdatedPermission.code(UPDATED_CODE);

        restPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPermission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPermission))
            )
            .andExpect(status().isOk());

        // Validate the Permission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPermissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPermission, permission),
            getPersistedPermission(permission)
        );
    }

    @Test
    @Transactional
    void fullUpdatePermissionWithPatch() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the permission using partial update
        Permission partialUpdatedPermission = new Permission();
        partialUpdatedPermission.setId(permission.getId());

        partialUpdatedPermission.code(UPDATED_CODE).label(UPDATED_LABEL);

        restPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPermission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPermission))
            )
            .andExpect(status().isOk());

        // Validate the Permission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPermissionUpdatableFieldsEquals(partialUpdatedPermission, getPersistedPermission(partialUpdatedPermission));
    }

    @Test
    @Transactional
    void patchNonExistingPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, permissionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(permissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(permissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permission.setId(longCount.incrementAndGet());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermissionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(permissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Permission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePermission() throws Exception {
        // Initialize the database
        insertedPermission = permissionRepository.saveAndFlush(permission);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the permission
        restPermissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, permission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return permissionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Permission getPersistedPermission(Permission permission) {
        return permissionRepository.findById(permission.getId()).orElseThrow();
    }

    protected void assertPersistedPermissionToMatchAllProperties(Permission expectedPermission) {
        assertPermissionAllPropertiesEquals(expectedPermission, getPersistedPermission(expectedPermission));
    }

    protected void assertPersistedPermissionToMatchUpdatableProperties(Permission expectedPermission) {
        assertPermissionAllUpdatablePropertiesEquals(expectedPermission, getPersistedPermission(expectedPermission));
    }
}
