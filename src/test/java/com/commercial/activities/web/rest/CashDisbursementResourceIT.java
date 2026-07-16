package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.CashDisbursementAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static com.commercial.activities.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.CashDisbursement;
import com.commercial.activities.domain.Company;
import com.commercial.activities.repository.CashDisbursementRepository;
import com.commercial.activities.service.dto.CashDisbursementDTO;
import com.commercial.activities.service.mapper.CashDisbursementMapper;
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
 * Integration tests for the {@link CashDisbursementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CashDisbursementResourceIT {

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.ofEpochMilli(1784236791515L);

    private static final String ENTITY_API_URL = "/api/cash-disbursements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CashDisbursementRepository cashDisbursementRepository;

    @Autowired
    private CashDisbursementMapper cashDisbursementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCashDisbursementMockMvc;

    private CashDisbursement cashDisbursement;

    private CashDisbursement insertedCashDisbursement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CashDisbursement createEntity() {
        return new CashDisbursement().reason(DEFAULT_REASON).amount(DEFAULT_AMOUNT).date(DEFAULT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CashDisbursement createUpdatedEntity() {
        return new CashDisbursement().reason(UPDATED_REASON).amount(UPDATED_AMOUNT).date(UPDATED_DATE);
    }

    @BeforeEach
    void initTest() {
        cashDisbursement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCashDisbursement != null) {
            cashDisbursementRepository.delete(insertedCashDisbursement);
            insertedCashDisbursement = null;
        }
    }

    @Test
    @Transactional
    void createCashDisbursement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CashDisbursement
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);
        var returnedCashDisbursementDTO = om.readValue(
            restCashDisbursementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashDisbursementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CashDisbursementDTO.class
        );

        // Validate the CashDisbursement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCashDisbursement = cashDisbursementMapper.toEntity(returnedCashDisbursementDTO);
        assertCashDisbursementUpdatableFieldsEquals(returnedCashDisbursement, getPersistedCashDisbursement(returnedCashDisbursement));

        insertedCashDisbursement = returnedCashDisbursement;
    }

    @Test
    @Transactional
    void createCashDisbursementWithExistingId() throws Exception {
        // Create the CashDisbursement with an existing ID
        cashDisbursement.setId(1L);
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCashDisbursementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashDisbursementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CashDisbursement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashDisbursement.setReason(null);

        // Create the CashDisbursement, which fails.
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);

        restCashDisbursementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashDisbursementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashDisbursement.setAmount(null);

        // Create the CashDisbursement, which fails.
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);

        restCashDisbursementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashDisbursementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashDisbursement.setDate(null);

        // Create the CashDisbursement, which fails.
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);

        restCashDisbursementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashDisbursementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCashDisbursements() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList
        restCashDisbursementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cashDisbursement.getId().intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getCashDisbursement() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get the cashDisbursement
        restCashDisbursementMockMvc
            .perform(get(ENTITY_API_URL_ID, cashDisbursement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cashDisbursement.getId().intValue()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getCashDisbursementsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        Long id = cashDisbursement.getId();

        defaultCashDisbursementFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCashDisbursementFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCashDisbursementFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where reason equals to
        defaultCashDisbursementFiltering("reason.equals=" + DEFAULT_REASON, "reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where reason in
        defaultCashDisbursementFiltering("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON, "reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where reason is not null
        defaultCashDisbursementFiltering("reason.specified=true", "reason.specified=false");
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where reason contains
        defaultCashDisbursementFiltering("reason.contains=" + DEFAULT_REASON, "reason.contains=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where reason does not contain
        defaultCashDisbursementFiltering("reason.doesNotContain=" + UPDATED_REASON, "reason.doesNotContain=" + DEFAULT_REASON);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where amount equals to
        defaultCashDisbursementFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where amount in
        defaultCashDisbursementFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where amount is not null
        defaultCashDisbursementFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where amount is greater than or equal to
        defaultCashDisbursementFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where amount is less than or equal to
        defaultCashDisbursementFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where amount is less than
        defaultCashDisbursementFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where amount is greater than
        defaultCashDisbursementFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where date equals to
        defaultCashDisbursementFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where date in
        defaultCashDisbursementFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        // Get all the cashDisbursementList where date is not null
        defaultCashDisbursementFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByCompanyIsEqualToSomething() throws Exception {
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            cashDisbursementRepository.saveAndFlush(cashDisbursement);
            company = CompanyResourceIT.createEntity();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        em.persist(company);
        em.flush();
        cashDisbursement.setCompany(company);
        cashDisbursementRepository.saveAndFlush(cashDisbursement);
        Long companyId = company.getId();
        // Get all the cashDisbursementList where company equals to companyId
        defaultCashDisbursementShouldBeFound("companyId.equals=" + companyId);

        // Get all the cashDisbursementList where company equals to (companyId + 1)
        defaultCashDisbursementShouldNotBeFound("companyId.equals=" + (companyId + 1));
    }

    @Test
    @Transactional
    void getAllCashDisbursementsByUserIsEqualToSomething() throws Exception {
        AppUser user;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            cashDisbursementRepository.saveAndFlush(cashDisbursement);
            user = AppUserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(user);
        em.flush();
        cashDisbursement.setUser(user);
        cashDisbursementRepository.saveAndFlush(cashDisbursement);
        Long userId = user.getId();
        // Get all the cashDisbursementList where user equals to userId
        defaultCashDisbursementShouldBeFound("userId.equals=" + userId);

        // Get all the cashDisbursementList where user equals to (userId + 1)
        defaultCashDisbursementShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultCashDisbursementFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCashDisbursementShouldBeFound(shouldBeFound);
        defaultCashDisbursementShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCashDisbursementShouldBeFound(String filter) throws Exception {
        restCashDisbursementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cashDisbursement.getId().intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));

        // Check, that the count call also returns 1
        restCashDisbursementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCashDisbursementShouldNotBeFound(String filter) throws Exception {
        restCashDisbursementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCashDisbursementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCashDisbursement() throws Exception {
        // Get the cashDisbursement
        restCashDisbursementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCashDisbursement() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashDisbursement
        CashDisbursement updatedCashDisbursement = cashDisbursementRepository.findById(cashDisbursement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCashDisbursement are not directly saved in db
        em.detach(updatedCashDisbursement);
        updatedCashDisbursement.reason(UPDATED_REASON).amount(UPDATED_AMOUNT).date(UPDATED_DATE);
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(updatedCashDisbursement);

        restCashDisbursementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cashDisbursementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashDisbursementDTO))
            )
            .andExpect(status().isOk());

        // Validate the CashDisbursement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCashDisbursementToMatchAllProperties(updatedCashDisbursement);
    }

    @Test
    @Transactional
    void putNonExistingCashDisbursement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashDisbursement.setId(longCount.incrementAndGet());

        // Create the CashDisbursement
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCashDisbursementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cashDisbursementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashDisbursementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashDisbursement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCashDisbursement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashDisbursement.setId(longCount.incrementAndGet());

        // Create the CashDisbursement
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashDisbursementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashDisbursementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashDisbursement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCashDisbursement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashDisbursement.setId(longCount.incrementAndGet());

        // Create the CashDisbursement
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashDisbursementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashDisbursementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CashDisbursement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCashDisbursementWithPatch() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashDisbursement using partial update
        CashDisbursement partialUpdatedCashDisbursement = new CashDisbursement();
        partialUpdatedCashDisbursement.setId(cashDisbursement.getId());

        partialUpdatedCashDisbursement.reason(UPDATED_REASON).date(UPDATED_DATE);

        restCashDisbursementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCashDisbursement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCashDisbursement))
            )
            .andExpect(status().isOk());

        // Validate the CashDisbursement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCashDisbursementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCashDisbursement, cashDisbursement),
            getPersistedCashDisbursement(cashDisbursement)
        );
    }

    @Test
    @Transactional
    void fullUpdateCashDisbursementWithPatch() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashDisbursement using partial update
        CashDisbursement partialUpdatedCashDisbursement = new CashDisbursement();
        partialUpdatedCashDisbursement.setId(cashDisbursement.getId());

        partialUpdatedCashDisbursement.reason(UPDATED_REASON).amount(UPDATED_AMOUNT).date(UPDATED_DATE);

        restCashDisbursementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCashDisbursement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCashDisbursement))
            )
            .andExpect(status().isOk());

        // Validate the CashDisbursement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCashDisbursementUpdatableFieldsEquals(
            partialUpdatedCashDisbursement,
            getPersistedCashDisbursement(partialUpdatedCashDisbursement)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCashDisbursement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashDisbursement.setId(longCount.incrementAndGet());

        // Create the CashDisbursement
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCashDisbursementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cashDisbursementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cashDisbursementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashDisbursement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCashDisbursement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashDisbursement.setId(longCount.incrementAndGet());

        // Create the CashDisbursement
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashDisbursementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cashDisbursementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashDisbursement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCashDisbursement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashDisbursement.setId(longCount.incrementAndGet());

        // Create the CashDisbursement
        CashDisbursementDTO cashDisbursementDTO = cashDisbursementMapper.toDto(cashDisbursement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashDisbursementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cashDisbursementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CashDisbursement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCashDisbursement() throws Exception {
        // Initialize the database
        insertedCashDisbursement = cashDisbursementRepository.saveAndFlush(cashDisbursement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cashDisbursement
        restCashDisbursementMockMvc
            .perform(delete(ENTITY_API_URL_ID, cashDisbursement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cashDisbursementRepository.count();
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

    protected CashDisbursement getPersistedCashDisbursement(CashDisbursement cashDisbursement) {
        return cashDisbursementRepository.findById(cashDisbursement.getId()).orElseThrow();
    }

    protected void assertPersistedCashDisbursementToMatchAllProperties(CashDisbursement expectedCashDisbursement) {
        assertCashDisbursementAllPropertiesEquals(expectedCashDisbursement, getPersistedCashDisbursement(expectedCashDisbursement));
    }

    protected void assertPersistedCashDisbursementToMatchUpdatableProperties(CashDisbursement expectedCashDisbursement) {
        assertCashDisbursementAllUpdatablePropertiesEquals(
            expectedCashDisbursement,
            getPersistedCashDisbursement(expectedCashDisbursement)
        );
    }
}
