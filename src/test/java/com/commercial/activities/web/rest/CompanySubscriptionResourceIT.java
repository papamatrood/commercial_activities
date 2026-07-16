package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.CompanySubscriptionAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.CompanySubscription;
import com.commercial.activities.domain.enumeration.CompanySubscriptionStatusEnum;
import com.commercial.activities.domain.enumeration.CompanySubscriptionTypeEnum;
import com.commercial.activities.repository.CompanySubscriptionRepository;
import com.commercial.activities.service.dto.CompanySubscriptionDTO;
import com.commercial.activities.service.mapper.CompanySubscriptionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * Integration tests for the {@link CompanySubscriptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CompanySubscriptionResourceIT {

    private static final CompanySubscriptionTypeEnum DEFAULT_TYPE = CompanySubscriptionTypeEnum.MONTHLY;
    private static final CompanySubscriptionTypeEnum UPDATED_TYPE = CompanySubscriptionTypeEnum.QUARTERLY;

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.ofEpochMilli(1784236791515L);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.ofEpochMilli(1784236791515L);

    private static final CompanySubscriptionStatusEnum DEFAULT_STATUS = CompanySubscriptionStatusEnum.ACTIVE;
    private static final CompanySubscriptionStatusEnum UPDATED_STATUS = CompanySubscriptionStatusEnum.EXPIRED;

    private static final String ENTITY_API_URL = "/api/company-subscriptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CompanySubscriptionRepository companySubscriptionRepository;

    @Autowired
    private CompanySubscriptionMapper companySubscriptionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompanySubscriptionMockMvc;

    private CompanySubscription companySubscription;

    private CompanySubscription insertedCompanySubscription;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompanySubscription createEntity() {
        return new CompanySubscription().type(DEFAULT_TYPE).startDate(DEFAULT_START_DATE).endDate(DEFAULT_END_DATE).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompanySubscription createUpdatedEntity() {
        return new CompanySubscription().type(UPDATED_TYPE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        companySubscription = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCompanySubscription != null) {
            companySubscriptionRepository.delete(insertedCompanySubscription);
            insertedCompanySubscription = null;
        }
    }

    @Test
    @Transactional
    void createCompanySubscription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CompanySubscription
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);
        var returnedCompanySubscriptionDTO = om.readValue(
            restCompanySubscriptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companySubscriptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CompanySubscriptionDTO.class
        );

        // Validate the CompanySubscription in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCompanySubscription = companySubscriptionMapper.toEntity(returnedCompanySubscriptionDTO);
        assertCompanySubscriptionUpdatableFieldsEquals(
            returnedCompanySubscription,
            getPersistedCompanySubscription(returnedCompanySubscription)
        );

        insertedCompanySubscription = returnedCompanySubscription;
    }

    @Test
    @Transactional
    void createCompanySubscriptionWithExistingId() throws Exception {
        // Create the CompanySubscription with an existing ID
        companySubscription.setId(1L);
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompanySubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companySubscriptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CompanySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        companySubscription.setType(null);

        // Create the CompanySubscription, which fails.
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);

        restCompanySubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companySubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        companySubscription.setStartDate(null);

        // Create the CompanySubscription, which fails.
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);

        restCompanySubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companySubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        companySubscription.setEndDate(null);

        // Create the CompanySubscription, which fails.
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);

        restCompanySubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companySubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCompanySubscriptions() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList
        restCompanySubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companySubscription.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getCompanySubscription() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get the companySubscription
        restCompanySubscriptionMockMvc
            .perform(get(ENTITY_API_URL_ID, companySubscription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(companySubscription.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getCompanySubscriptionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        Long id = companySubscription.getId();

        defaultCompanySubscriptionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCompanySubscriptionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCompanySubscriptionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where type equals to
        defaultCompanySubscriptionFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where type in
        defaultCompanySubscriptionFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where type is not null
        defaultCompanySubscriptionFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where startDate equals to
        defaultCompanySubscriptionFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where startDate in
        defaultCompanySubscriptionFiltering(
            "startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE,
            "startDate.in=" + UPDATED_START_DATE
        );
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where startDate is not null
        defaultCompanySubscriptionFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where endDate equals to
        defaultCompanySubscriptionFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where endDate in
        defaultCompanySubscriptionFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where endDate is not null
        defaultCompanySubscriptionFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where status equals to
        defaultCompanySubscriptionFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where status in
        defaultCompanySubscriptionFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        // Get all the companySubscriptionList where status is not null
        defaultCompanySubscriptionFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCompanySubscriptionsByCompanyIsEqualToSomething() throws Exception {
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            companySubscriptionRepository.saveAndFlush(companySubscription);
            company = CompanyResourceIT.createEntity();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        em.persist(company);
        em.flush();
        companySubscription.setCompany(company);
        companySubscriptionRepository.saveAndFlush(companySubscription);
        Long companyId = company.getId();
        // Get all the companySubscriptionList where company equals to companyId
        defaultCompanySubscriptionShouldBeFound("companyId.equals=" + companyId);

        // Get all the companySubscriptionList where company equals to (companyId + 1)
        defaultCompanySubscriptionShouldNotBeFound("companyId.equals=" + (companyId + 1));
    }

    private void defaultCompanySubscriptionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCompanySubscriptionShouldBeFound(shouldBeFound);
        defaultCompanySubscriptionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCompanySubscriptionShouldBeFound(String filter) throws Exception {
        restCompanySubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(companySubscription.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restCompanySubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCompanySubscriptionShouldNotBeFound(String filter) throws Exception {
        restCompanySubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCompanySubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCompanySubscription() throws Exception {
        // Get the companySubscription
        restCompanySubscriptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCompanySubscription() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the companySubscription
        CompanySubscription updatedCompanySubscription = companySubscriptionRepository.findById(companySubscription.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCompanySubscription are not directly saved in db
        em.detach(updatedCompanySubscription);
        updatedCompanySubscription.type(UPDATED_TYPE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(updatedCompanySubscription);

        restCompanySubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, companySubscriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(companySubscriptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the CompanySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCompanySubscriptionToMatchAllProperties(updatedCompanySubscription);
    }

    @Test
    @Transactional
    void putNonExistingCompanySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        companySubscription.setId(longCount.incrementAndGet());

        // Create the CompanySubscription
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanySubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, companySubscriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(companySubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompanySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        companySubscription.setId(longCount.incrementAndGet());

        // Create the CompanySubscription
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanySubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(companySubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompanySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        companySubscription.setId(longCount.incrementAndGet());

        // Create the CompanySubscription
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanySubscriptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(companySubscriptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompanySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCompanySubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the companySubscription using partial update
        CompanySubscription partialUpdatedCompanySubscription = new CompanySubscription();
        partialUpdatedCompanySubscription.setId(companySubscription.getId());

        partialUpdatedCompanySubscription.type(UPDATED_TYPE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);

        restCompanySubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompanySubscription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompanySubscription))
            )
            .andExpect(status().isOk());

        // Validate the CompanySubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompanySubscriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCompanySubscription, companySubscription),
            getPersistedCompanySubscription(companySubscription)
        );
    }

    @Test
    @Transactional
    void fullUpdateCompanySubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the companySubscription using partial update
        CompanySubscription partialUpdatedCompanySubscription = new CompanySubscription();
        partialUpdatedCompanySubscription.setId(companySubscription.getId());

        partialUpdatedCompanySubscription.type(UPDATED_TYPE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);

        restCompanySubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompanySubscription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCompanySubscription))
            )
            .andExpect(status().isOk());

        // Validate the CompanySubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCompanySubscriptionUpdatableFieldsEquals(
            partialUpdatedCompanySubscription,
            getPersistedCompanySubscription(partialUpdatedCompanySubscription)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCompanySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        companySubscription.setId(longCount.incrementAndGet());

        // Create the CompanySubscription
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanySubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, companySubscriptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(companySubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompanySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        companySubscription.setId(longCount.incrementAndGet());

        // Create the CompanySubscription
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanySubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(companySubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompanySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompanySubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        companySubscription.setId(longCount.incrementAndGet());

        // Create the CompanySubscription
        CompanySubscriptionDTO companySubscriptionDTO = companySubscriptionMapper.toDto(companySubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompanySubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(companySubscriptionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompanySubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCompanySubscription() throws Exception {
        // Initialize the database
        insertedCompanySubscription = companySubscriptionRepository.saveAndFlush(companySubscription);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the companySubscription
        restCompanySubscriptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, companySubscription.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return companySubscriptionRepository.count();
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

    protected CompanySubscription getPersistedCompanySubscription(CompanySubscription companySubscription) {
        return companySubscriptionRepository.findById(companySubscription.getId()).orElseThrow();
    }

    protected void assertPersistedCompanySubscriptionToMatchAllProperties(CompanySubscription expectedCompanySubscription) {
        assertCompanySubscriptionAllPropertiesEquals(
            expectedCompanySubscription,
            getPersistedCompanySubscription(expectedCompanySubscription)
        );
    }

    protected void assertPersistedCompanySubscriptionToMatchUpdatableProperties(CompanySubscription expectedCompanySubscription) {
        assertCompanySubscriptionAllUpdatablePropertiesEquals(
            expectedCompanySubscription,
            getPersistedCompanySubscription(expectedCompanySubscription)
        );
    }
}
