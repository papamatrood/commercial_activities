package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.SupplierAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.Supplier;
import com.commercial.activities.repository.SupplierRepository;
import com.commercial.activities.service.dto.SupplierDTO;
import com.commercial.activities.service.mapper.SupplierMapper;
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
 * Integration tests for the {@link SupplierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SupplierResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/suppliers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSupplierMockMvc;

    private Supplier supplier;

    private Supplier insertedSupplier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Supplier createEntity() {
        return new Supplier().name(DEFAULT_NAME).contact(DEFAULT_CONTACT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Supplier createUpdatedEntity() {
        return new Supplier().name(UPDATED_NAME).contact(UPDATED_CONTACT);
    }

    @BeforeEach
    void initTest() {
        supplier = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSupplier != null) {
            supplierRepository.delete(insertedSupplier);
            insertedSupplier = null;
        }
    }

    @Test
    @Transactional
    void createSupplier() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);
        var returnedSupplierDTO = om.readValue(
            restSupplierMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SupplierDTO.class
        );

        // Validate the Supplier in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSupplier = supplierMapper.toEntity(returnedSupplierDTO);
        assertSupplierUpdatableFieldsEquals(returnedSupplier, getPersistedSupplier(returnedSupplier));

        insertedSupplier = returnedSupplier;
    }

    @Test
    @Transactional
    void createSupplierWithExistingId() throws Exception {
        // Create the Supplier with an existing ID
        supplier.setId(1L);
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplier.setName(null);

        // Create the Supplier, which fails.
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        restSupplierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSuppliers() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplier.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)));
    }

    @Test
    @Transactional
    void getSupplier() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get the supplier
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL_ID, supplier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supplier.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.contact").value(DEFAULT_CONTACT));
    }

    @Test
    @Transactional
    void getSuppliersByIdFiltering() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        Long id = supplier.getId();

        defaultSupplierFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSupplierFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSupplierFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSuppliersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where name equals to
        defaultSupplierFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where name in
        defaultSupplierFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where name is not null
        defaultSupplierFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliersByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where name contains
        defaultSupplierFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where name does not contain
        defaultSupplierFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllSuppliersByContactIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where contact equals to
        defaultSupplierFiltering("contact.equals=" + DEFAULT_CONTACT, "contact.equals=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllSuppliersByContactIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where contact in
        defaultSupplierFiltering("contact.in=" + DEFAULT_CONTACT + "," + UPDATED_CONTACT, "contact.in=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllSuppliersByContactIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where contact is not null
        defaultSupplierFiltering("contact.specified=true", "contact.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliersByContactContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where contact contains
        defaultSupplierFiltering("contact.contains=" + DEFAULT_CONTACT, "contact.contains=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllSuppliersByContactNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        // Get all the supplierList where contact does not contain
        defaultSupplierFiltering("contact.doesNotContain=" + UPDATED_CONTACT, "contact.doesNotContain=" + DEFAULT_CONTACT);
    }

    private void defaultSupplierFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSupplierShouldBeFound(shouldBeFound);
        defaultSupplierShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSupplierShouldBeFound(String filter) throws Exception {
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplier.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)));

        // Check, that the count call also returns 1
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSupplierShouldNotBeFound(String filter) throws Exception {
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSupplierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSupplier() throws Exception {
        // Get the supplier
        restSupplierMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSupplier() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplier
        Supplier updatedSupplier = supplierRepository.findById(supplier.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSupplier are not directly saved in db
        em.detach(updatedSupplier);
        updatedSupplier.name(UPDATED_NAME).contact(UPDATED_CONTACT);
        SupplierDTO supplierDTO = supplierMapper.toDto(updatedSupplier);

        restSupplierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isOk());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSupplierToMatchAllProperties(updatedSupplier);
    }

    @Test
    @Transactional
    void putNonExistingSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSupplierWithPatch() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplier using partial update
        Supplier partialUpdatedSupplier = new Supplier();
        partialUpdatedSupplier.setId(supplier.getId());

        restSupplierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupplier))
            )
            .andExpect(status().isOk());

        // Validate the Supplier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupplierUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSupplier, supplier), getPersistedSupplier(supplier));
    }

    @Test
    @Transactional
    void fullUpdateSupplierWithPatch() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplier using partial update
        Supplier partialUpdatedSupplier = new Supplier();
        partialUpdatedSupplier.setId(supplier.getId());

        partialUpdatedSupplier.name(UPDATED_NAME).contact(UPDATED_CONTACT);

        restSupplierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupplier))
            )
            .andExpect(status().isOk());

        // Validate the Supplier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupplierUpdatableFieldsEquals(partialUpdatedSupplier, getPersistedSupplier(partialUpdatedSupplier));
    }

    @Test
    @Transactional
    void patchNonExistingSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, supplierDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supplierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSupplier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplier.setId(longCount.incrementAndGet());

        // Create the Supplier
        SupplierDTO supplierDTO = supplierMapper.toDto(supplier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplierMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(supplierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Supplier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSupplier() throws Exception {
        // Initialize the database
        insertedSupplier = supplierRepository.saveAndFlush(supplier);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the supplier
        restSupplierMockMvc
            .perform(delete(ENTITY_API_URL_ID, supplier.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return supplierRepository.count();
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

    protected Supplier getPersistedSupplier(Supplier supplier) {
        return supplierRepository.findById(supplier.getId()).orElseThrow();
    }

    protected void assertPersistedSupplierToMatchAllProperties(Supplier expectedSupplier) {
        assertSupplierAllPropertiesEquals(expectedSupplier, getPersistedSupplier(expectedSupplier));
    }

    protected void assertPersistedSupplierToMatchUpdatableProperties(Supplier expectedSupplier) {
        assertSupplierAllUpdatablePropertiesEquals(expectedSupplier, getPersistedSupplier(expectedSupplier));
    }
}
