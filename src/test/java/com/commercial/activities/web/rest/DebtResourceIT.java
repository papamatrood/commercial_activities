package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.DebtAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static com.commercial.activities.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.Debt;
import com.commercial.activities.domain.Sale;
import com.commercial.activities.repository.DebtRepository;
import com.commercial.activities.service.dto.DebtDTO;
import com.commercial.activities.service.mapper.DebtMapper;
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
 * Integration tests for the {@link DebtResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DebtResourceIT {

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_AMOUNT_PAID = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_PAID = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT_PAID = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_REMAINING_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_REMAINING_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_REMAINING_AMOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.ofEpochMilli(1784236791515L);

    private static final String ENTITY_API_URL = "/api/debts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private DebtMapper debtMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDebtMockMvc;

    private Debt debt;

    private Debt insertedDebt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Debt createEntity() {
        return new Debt()
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .amountPaid(DEFAULT_AMOUNT_PAID)
            .remainingAmount(DEFAULT_REMAINING_AMOUNT)
            .date(DEFAULT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Debt createUpdatedEntity() {
        return new Debt()
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .remainingAmount(UPDATED_REMAINING_AMOUNT)
            .date(UPDATED_DATE);
    }

    @BeforeEach
    void initTest() {
        debt = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDebt != null) {
            debtRepository.delete(insertedDebt);
            insertedDebt = null;
        }
    }

    @Test
    @Transactional
    void createDebt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Debt
        DebtDTO debtDTO = debtMapper.toDto(debt);
        var returnedDebtDTO = om.readValue(
            restDebtMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DebtDTO.class
        );

        // Validate the Debt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDebt = debtMapper.toEntity(returnedDebtDTO);
        assertDebtUpdatableFieldsEquals(returnedDebt, getPersistedDebt(returnedDebt));

        insertedDebt = returnedDebt;
    }

    @Test
    @Transactional
    void createDebtWithExistingId() throws Exception {
        // Create the Debt with an existing ID
        debt.setId(1L);
        DebtDTO debtDTO = debtMapper.toDto(debt);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDebtMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Debt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        debt.setTotalAmount(null);

        // Create the Debt, which fails.
        DebtDTO debtDTO = debtMapper.toDto(debt);

        restDebtMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        debt.setDate(null);

        // Create the Debt, which fails.
        DebtDTO debtDTO = debtMapper.toDto(debt);

        restDebtMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDebts() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList
        restDebtMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(debt.getId().intValue())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID))))
            .andExpect(jsonPath("$.[*].remainingAmount").value(hasItem(sameNumber(DEFAULT_REMAINING_AMOUNT))))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getDebt() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get the debt
        restDebtMockMvc
            .perform(get(ENTITY_API_URL_ID, debt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(debt.getId().intValue()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.amountPaid").value(sameNumber(DEFAULT_AMOUNT_PAID)))
            .andExpect(jsonPath("$.remainingAmount").value(sameNumber(DEFAULT_REMAINING_AMOUNT)))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getDebtsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        Long id = debt.getId();

        defaultDebtFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDebtFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDebtFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDebtsByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where totalAmount equals to
        defaultDebtFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDebtsByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where totalAmount in
        defaultDebtFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtsByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where totalAmount is not null
        defaultDebtFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllDebtsByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where totalAmount is greater than or equal to
        defaultDebtFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtsByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where totalAmount is less than or equal to
        defaultDebtFiltering("totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDebtsByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where totalAmount is less than
        defaultDebtFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDebtsByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where totalAmount is greater than
        defaultDebtFiltering("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT, "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDebtsByAmountPaidIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where amountPaid equals to
        defaultDebtFiltering("amountPaid.equals=" + DEFAULT_AMOUNT_PAID, "amountPaid.equals=" + UPDATED_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllDebtsByAmountPaidIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where amountPaid in
        defaultDebtFiltering("amountPaid.in=" + DEFAULT_AMOUNT_PAID + "," + UPDATED_AMOUNT_PAID, "amountPaid.in=" + UPDATED_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllDebtsByAmountPaidIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where amountPaid is not null
        defaultDebtFiltering("amountPaid.specified=true", "amountPaid.specified=false");
    }

    @Test
    @Transactional
    void getAllDebtsByAmountPaidIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where amountPaid is greater than or equal to
        defaultDebtFiltering(
            "amountPaid.greaterThanOrEqual=" + DEFAULT_AMOUNT_PAID,
            "amountPaid.greaterThanOrEqual=" + UPDATED_AMOUNT_PAID
        );
    }

    @Test
    @Transactional
    void getAllDebtsByAmountPaidIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where amountPaid is less than or equal to
        defaultDebtFiltering("amountPaid.lessThanOrEqual=" + DEFAULT_AMOUNT_PAID, "amountPaid.lessThanOrEqual=" + SMALLER_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllDebtsByAmountPaidIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where amountPaid is less than
        defaultDebtFiltering("amountPaid.lessThan=" + UPDATED_AMOUNT_PAID, "amountPaid.lessThan=" + DEFAULT_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllDebtsByAmountPaidIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where amountPaid is greater than
        defaultDebtFiltering("amountPaid.greaterThan=" + SMALLER_AMOUNT_PAID, "amountPaid.greaterThan=" + DEFAULT_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllDebtsByRemainingAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where remainingAmount equals to
        defaultDebtFiltering("remainingAmount.equals=" + DEFAULT_REMAINING_AMOUNT, "remainingAmount.equals=" + UPDATED_REMAINING_AMOUNT);
    }

    @Test
    @Transactional
    void getAllDebtsByRemainingAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where remainingAmount in
        defaultDebtFiltering(
            "remainingAmount.in=" + DEFAULT_REMAINING_AMOUNT + "," + UPDATED_REMAINING_AMOUNT,
            "remainingAmount.in=" + UPDATED_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtsByRemainingAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where remainingAmount is not null
        defaultDebtFiltering("remainingAmount.specified=true", "remainingAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllDebtsByRemainingAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where remainingAmount is greater than or equal to
        defaultDebtFiltering(
            "remainingAmount.greaterThanOrEqual=" + DEFAULT_REMAINING_AMOUNT,
            "remainingAmount.greaterThanOrEqual=" + UPDATED_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtsByRemainingAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where remainingAmount is less than or equal to
        defaultDebtFiltering(
            "remainingAmount.lessThanOrEqual=" + DEFAULT_REMAINING_AMOUNT,
            "remainingAmount.lessThanOrEqual=" + SMALLER_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtsByRemainingAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where remainingAmount is less than
        defaultDebtFiltering(
            "remainingAmount.lessThan=" + UPDATED_REMAINING_AMOUNT,
            "remainingAmount.lessThan=" + DEFAULT_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtsByRemainingAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where remainingAmount is greater than
        defaultDebtFiltering(
            "remainingAmount.greaterThan=" + SMALLER_REMAINING_AMOUNT,
            "remainingAmount.greaterThan=" + DEFAULT_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where date equals to
        defaultDebtFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDebtsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where date in
        defaultDebtFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDebtsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        // Get all the debtList where date is not null
        defaultDebtFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllDebtsByCompanyIsEqualToSomething() throws Exception {
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            debtRepository.saveAndFlush(debt);
            company = CompanyResourceIT.createEntity();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        em.persist(company);
        em.flush();
        debt.setCompany(company);
        debtRepository.saveAndFlush(debt);
        Long companyId = company.getId();
        // Get all the debtList where company equals to companyId
        defaultDebtShouldBeFound("companyId.equals=" + companyId);

        // Get all the debtList where company equals to (companyId + 1)
        defaultDebtShouldNotBeFound("companyId.equals=" + (companyId + 1));
    }

    @Test
    @Transactional
    void getAllDebtsBySaleIsEqualToSomething() throws Exception {
        Sale sale;
        if (TestUtil.findAll(em, Sale.class).isEmpty()) {
            debtRepository.saveAndFlush(debt);
            sale = SaleResourceIT.createEntity();
        } else {
            sale = TestUtil.findAll(em, Sale.class).get(0);
        }
        em.persist(sale);
        em.flush();
        debt.setSale(sale);
        debtRepository.saveAndFlush(debt);
        Long saleId = sale.getId();
        // Get all the debtList where sale equals to saleId
        defaultDebtShouldBeFound("saleId.equals=" + saleId);

        // Get all the debtList where sale equals to (saleId + 1)
        defaultDebtShouldNotBeFound("saleId.equals=" + (saleId + 1));
    }

    private void defaultDebtFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDebtShouldBeFound(shouldBeFound);
        defaultDebtShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDebtShouldBeFound(String filter) throws Exception {
        restDebtMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(debt.getId().intValue())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID))))
            .andExpect(jsonPath("$.[*].remainingAmount").value(hasItem(sameNumber(DEFAULT_REMAINING_AMOUNT))))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));

        // Check, that the count call also returns 1
        restDebtMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDebtShouldNotBeFound(String filter) throws Exception {
        restDebtMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDebtMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDebt() throws Exception {
        // Get the debt
        restDebtMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDebt() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the debt
        Debt updatedDebt = debtRepository.findById(debt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDebt are not directly saved in db
        em.detach(updatedDebt);
        updatedDebt
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .remainingAmount(UPDATED_REMAINING_AMOUNT)
            .date(UPDATED_DATE);
        DebtDTO debtDTO = debtMapper.toDto(updatedDebt);

        restDebtMockMvc
            .perform(put(ENTITY_API_URL_ID, debtDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtDTO)))
            .andExpect(status().isOk());

        // Validate the Debt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDebtToMatchAllProperties(updatedDebt);
    }

    @Test
    @Transactional
    void putNonExistingDebt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debt.setId(longCount.incrementAndGet());

        // Create the Debt
        DebtDTO debtDTO = debtMapper.toDto(debt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDebtMockMvc
            .perform(put(ENTITY_API_URL_ID, debtDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Debt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDebt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debt.setId(longCount.incrementAndGet());

        // Create the Debt
        DebtDTO debtDTO = debtMapper.toDto(debt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDebtMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(debtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Debt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDebt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debt.setId(longCount.incrementAndGet());

        // Create the Debt
        DebtDTO debtDTO = debtMapper.toDto(debt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDebtMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Debt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDebtWithPatch() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the debt using partial update
        Debt partialUpdatedDebt = new Debt();
        partialUpdatedDebt.setId(debt.getId());

        partialUpdatedDebt.remainingAmount(UPDATED_REMAINING_AMOUNT);

        restDebtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDebt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDebt))
            )
            .andExpect(status().isOk());

        // Validate the Debt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDebtUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDebt, debt), getPersistedDebt(debt));
    }

    @Test
    @Transactional
    void fullUpdateDebtWithPatch() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the debt using partial update
        Debt partialUpdatedDebt = new Debt();
        partialUpdatedDebt.setId(debt.getId());

        partialUpdatedDebt
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .remainingAmount(UPDATED_REMAINING_AMOUNT)
            .date(UPDATED_DATE);

        restDebtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDebt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDebt))
            )
            .andExpect(status().isOk());

        // Validate the Debt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDebtUpdatableFieldsEquals(partialUpdatedDebt, getPersistedDebt(partialUpdatedDebt));
    }

    @Test
    @Transactional
    void patchNonExistingDebt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debt.setId(longCount.incrementAndGet());

        // Create the Debt
        DebtDTO debtDTO = debtMapper.toDto(debt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDebtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, debtDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(debtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Debt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDebt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debt.setId(longCount.incrementAndGet());

        // Create the Debt
        DebtDTO debtDTO = debtMapper.toDto(debt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDebtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(debtDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Debt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDebt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debt.setId(longCount.incrementAndGet());

        // Create the Debt
        DebtDTO debtDTO = debtMapper.toDto(debt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDebtMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(debtDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Debt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDebt() throws Exception {
        // Initialize the database
        insertedDebt = debtRepository.saveAndFlush(debt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the debt
        restDebtMockMvc
            .perform(delete(ENTITY_API_URL_ID, debt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return debtRepository.count();
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

    protected Debt getPersistedDebt(Debt debt) {
        return debtRepository.findById(debt.getId()).orElseThrow();
    }

    protected void assertPersistedDebtToMatchAllProperties(Debt expectedDebt) {
        assertDebtAllPropertiesEquals(expectedDebt, getPersistedDebt(expectedDebt));
    }

    protected void assertPersistedDebtToMatchUpdatableProperties(Debt expectedDebt) {
        assertDebtAllUpdatablePropertiesEquals(expectedDebt, getPersistedDebt(expectedDebt));
    }
}
