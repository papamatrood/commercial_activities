package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.CashCollectionAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static com.commercial.activities.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.CashCollection;
import com.commercial.activities.domain.Company;
import com.commercial.activities.repository.CashCollectionRepository;
import com.commercial.activities.service.dto.CashCollectionDTO;
import com.commercial.activities.service.mapper.CashCollectionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link CashCollectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CashCollectionResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.ofEpochMilli(1784236791515L);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/cash-collections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CashCollectionRepository cashCollectionRepository;

    @Autowired
    private CashCollectionMapper cashCollectionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCashCollectionMockMvc;

    private CashCollection cashCollection;

    private CashCollection insertedCashCollection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CashCollection createEntity() {
        return new CashCollection().date(DEFAULT_DATE).amount(DEFAULT_AMOUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CashCollection createUpdatedEntity() {
        return new CashCollection().date(UPDATED_DATE).amount(UPDATED_AMOUNT);
    }

    @BeforeEach
    void initTest() {
        cashCollection = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCashCollection != null) {
            cashCollectionRepository.delete(insertedCashCollection);
            insertedCashCollection = null;
        }
    }

    @Test
    @Transactional
    void createCashCollection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CashCollection
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(cashCollection);
        var returnedCashCollectionDTO = om.readValue(
            restCashCollectionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashCollectionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CashCollectionDTO.class
        );

        // Validate the CashCollection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCashCollection = cashCollectionMapper.toEntity(returnedCashCollectionDTO);
        assertCashCollectionUpdatableFieldsEquals(returnedCashCollection, getPersistedCashCollection(returnedCashCollection));

        insertedCashCollection = returnedCashCollection;
    }

    @Test
    @Transactional
    void createCashCollectionWithExistingId() throws Exception {
        // Create the CashCollection with an existing ID
        cashCollection.setId(1L);
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(cashCollection);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCashCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashCollectionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CashCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashCollection.setDate(null);

        // Create the CashCollection, which fails.
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(cashCollection);

        restCashCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashCollectionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashCollection.setAmount(null);

        // Create the CashCollection, which fails.
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(cashCollection);

        restCashCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashCollectionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCashCollections() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList
        restCashCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cashCollection.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))));
    }

    @Test
    @Transactional
    void getCashCollection() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get the cashCollection
        restCashCollectionMockMvc
            .perform(get(ENTITY_API_URL_ID, cashCollection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cashCollection.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)));
    }

    @Test
    @Transactional
    void getCashCollectionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        Long id = cashCollection.getId();

        defaultCashCollectionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCashCollectionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCashCollectionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCashCollectionsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList where date equals to
        defaultCashCollectionFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCashCollectionsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList where date in
        defaultCashCollectionFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCashCollectionsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList where date is not null
        defaultCashCollectionFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllCashCollectionsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList where amount equals to
        defaultCashCollectionFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashCollectionsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList where amount in
        defaultCashCollectionFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashCollectionsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList where amount is not null
        defaultCashCollectionFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllCashCollectionsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList where amount is greater than or equal to
        defaultCashCollectionFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashCollectionsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList where amount is less than or equal to
        defaultCashCollectionFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashCollectionsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList where amount is less than
        defaultCashCollectionFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashCollectionsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        // Get all the cashCollectionList where amount is greater than
        defaultCashCollectionFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashCollectionsByCompanyIsEqualToSomething() throws Exception {
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            cashCollectionRepository.saveAndFlush(cashCollection);
            company = CompanyResourceIT.createEntity();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        em.persist(company);
        em.flush();
        cashCollection.setCompany(company);
        cashCollectionRepository.saveAndFlush(cashCollection);
        Long companyId = company.getId();
        // Get all the cashCollectionList where company equals to companyId
        defaultCashCollectionShouldBeFound("companyId.equals=" + companyId);

        // Get all the cashCollectionList where company equals to (companyId + 1)
        defaultCashCollectionShouldNotBeFound("companyId.equals=" + (companyId + 1));
    }

    @Test
    @Transactional
    void getAllCashCollectionsByUserIsEqualToSomething() throws Exception {
        AppUser user;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            cashCollectionRepository.saveAndFlush(cashCollection);
            user = AppUserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(user);
        em.flush();
        cashCollection.setUser(user);
        cashCollectionRepository.saveAndFlush(cashCollection);
        Long userId = user.getId();
        // Get all the cashCollectionList where user equals to userId
        defaultCashCollectionShouldBeFound("userId.equals=" + userId);

        // Get all the cashCollectionList where user equals to (userId + 1)
        defaultCashCollectionShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultCashCollectionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCashCollectionShouldBeFound(shouldBeFound);
        defaultCashCollectionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCashCollectionShouldBeFound(String filter) throws Exception {
        restCashCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cashCollection.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))));

        // Check, that the count call also returns 1
        restCashCollectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCashCollectionShouldNotBeFound(String filter) throws Exception {
        restCashCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCashCollectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCashCollection() throws Exception {
        // Get the cashCollection
        restCashCollectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCashCollection() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashCollection
        CashCollection updatedCashCollection = cashCollectionRepository.findById(cashCollection.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCashCollection are not directly saved in db
        em.detach(updatedCashCollection);
        updatedCashCollection.date(UPDATED_DATE).amount(UPDATED_AMOUNT);
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(updatedCashCollection);

        restCashCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cashCollectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashCollectionDTO))
            )
            .andExpect(status().isOk());

        // Validate the CashCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCashCollectionToMatchAllProperties(updatedCashCollection);
    }

    @Test
    @Transactional
    void putNonExistingCashCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashCollection.setId(longCount.incrementAndGet());

        // Create the CashCollection
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(cashCollection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCashCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cashCollectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCashCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashCollection.setId(longCount.incrementAndGet());

        // Create the CashCollection
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(cashCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCashCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashCollection.setId(longCount.incrementAndGet());

        // Create the CashCollection
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(cashCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashCollectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashCollectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CashCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCashCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashCollection using partial update
        CashCollection partialUpdatedCashCollection = new CashCollection();
        partialUpdatedCashCollection.setId(cashCollection.getId());

        restCashCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCashCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCashCollection))
            )
            .andExpect(status().isOk());

        // Validate the CashCollection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCashCollectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCashCollection, cashCollection),
            getPersistedCashCollection(cashCollection)
        );
    }

    @Test
    @Transactional
    void fullUpdateCashCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashCollection using partial update
        CashCollection partialUpdatedCashCollection = new CashCollection();
        partialUpdatedCashCollection.setId(cashCollection.getId());

        partialUpdatedCashCollection.date(UPDATED_DATE).amount(UPDATED_AMOUNT);

        restCashCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCashCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCashCollection))
            )
            .andExpect(status().isOk());

        // Validate the CashCollection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCashCollectionUpdatableFieldsEquals(partialUpdatedCashCollection, getPersistedCashCollection(partialUpdatedCashCollection));
    }

    @Test
    @Transactional
    void patchNonExistingCashCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashCollection.setId(longCount.incrementAndGet());

        // Create the CashCollection
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(cashCollection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCashCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cashCollectionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cashCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCashCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashCollection.setId(longCount.incrementAndGet());

        // Create the CashCollection
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(cashCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cashCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCashCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashCollection.setId(longCount.incrementAndGet());

        // Create the CashCollection
        CashCollectionDTO cashCollectionDTO = cashCollectionMapper.toDto(cashCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashCollectionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cashCollectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CashCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCashCollection() throws Exception {
        // Initialize the database
        insertedCashCollection = cashCollectionRepository.saveAndFlush(cashCollection);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cashCollection
        restCashCollectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, cashCollection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cashCollectionRepository.count();
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

    protected CashCollection getPersistedCashCollection(CashCollection cashCollection) {
        return cashCollectionRepository.findById(cashCollection.getId()).orElseThrow();
    }

    protected void assertPersistedCashCollectionToMatchAllProperties(CashCollection expectedCashCollection) {
        assertCashCollectionAllPropertiesEquals(expectedCashCollection, getPersistedCashCollection(expectedCashCollection));
    }

    protected void assertPersistedCashCollectionToMatchUpdatableProperties(CashCollection expectedCashCollection) {
        assertCashCollectionAllUpdatablePropertiesEquals(expectedCashCollection, getPersistedCashCollection(expectedCashCollection));
    }
}
