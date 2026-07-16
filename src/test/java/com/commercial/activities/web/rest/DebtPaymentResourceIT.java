package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.DebtPaymentAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static com.commercial.activities.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.Debt;
import com.commercial.activities.domain.DebtPayment;
import com.commercial.activities.repository.DebtPaymentRepository;
import com.commercial.activities.service.dto.DebtPaymentDTO;
import com.commercial.activities.service.mapper.DebtPaymentMapper;
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
 * Integration tests for the {@link DebtPaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DebtPaymentResourceIT {

    private static final BigDecimal DEFAULT_AMOUNT_PAID = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_PAID = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT_PAID = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_REMAINING_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_REMAINING_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_REMAINING_AMOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.ofEpochMilli(1784236791515L);

    private static final String ENTITY_API_URL = "/api/debt-payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DebtPaymentRepository debtPaymentRepository;

    @Autowired
    private DebtPaymentMapper debtPaymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDebtPaymentMockMvc;

    private DebtPayment debtPayment;

    private DebtPayment insertedDebtPayment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DebtPayment createEntity() {
        return new DebtPayment().amountPaid(DEFAULT_AMOUNT_PAID).remainingAmount(DEFAULT_REMAINING_AMOUNT).date(DEFAULT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DebtPayment createUpdatedEntity() {
        return new DebtPayment().amountPaid(UPDATED_AMOUNT_PAID).remainingAmount(UPDATED_REMAINING_AMOUNT).date(UPDATED_DATE);
    }

    @BeforeEach
    void initTest() {
        debtPayment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDebtPayment != null) {
            debtPaymentRepository.delete(insertedDebtPayment);
            insertedDebtPayment = null;
        }
    }

    @Test
    @Transactional
    void createDebtPayment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DebtPayment
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(debtPayment);
        var returnedDebtPaymentDTO = om.readValue(
            restDebtPaymentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtPaymentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DebtPaymentDTO.class
        );

        // Validate the DebtPayment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDebtPayment = debtPaymentMapper.toEntity(returnedDebtPaymentDTO);
        assertDebtPaymentUpdatableFieldsEquals(returnedDebtPayment, getPersistedDebtPayment(returnedDebtPayment));

        insertedDebtPayment = returnedDebtPayment;
    }

    @Test
    @Transactional
    void createDebtPaymentWithExistingId() throws Exception {
        // Create the DebtPayment with an existing ID
        debtPayment.setId(1L);
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(debtPayment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDebtPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtPaymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DebtPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAmountPaidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        debtPayment.setAmountPaid(null);

        // Create the DebtPayment, which fails.
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(debtPayment);

        restDebtPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtPaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        debtPayment.setDate(null);

        // Create the DebtPayment, which fails.
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(debtPayment);

        restDebtPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtPaymentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDebtPayments() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList
        restDebtPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(debtPayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID))))
            .andExpect(jsonPath("$.[*].remainingAmount").value(hasItem(sameNumber(DEFAULT_REMAINING_AMOUNT))))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getDebtPayment() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get the debtPayment
        restDebtPaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, debtPayment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(debtPayment.getId().intValue()))
            .andExpect(jsonPath("$.amountPaid").value(sameNumber(DEFAULT_AMOUNT_PAID)))
            .andExpect(jsonPath("$.remainingAmount").value(sameNumber(DEFAULT_REMAINING_AMOUNT)))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getDebtPaymentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        Long id = debtPayment.getId();

        defaultDebtPaymentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDebtPaymentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDebtPaymentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByAmountPaidIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where amountPaid equals to
        defaultDebtPaymentFiltering("amountPaid.equals=" + DEFAULT_AMOUNT_PAID, "amountPaid.equals=" + UPDATED_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByAmountPaidIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where amountPaid in
        defaultDebtPaymentFiltering(
            "amountPaid.in=" + DEFAULT_AMOUNT_PAID + "," + UPDATED_AMOUNT_PAID,
            "amountPaid.in=" + UPDATED_AMOUNT_PAID
        );
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByAmountPaidIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where amountPaid is not null
        defaultDebtPaymentFiltering("amountPaid.specified=true", "amountPaid.specified=false");
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByAmountPaidIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where amountPaid is greater than or equal to
        defaultDebtPaymentFiltering(
            "amountPaid.greaterThanOrEqual=" + DEFAULT_AMOUNT_PAID,
            "amountPaid.greaterThanOrEqual=" + UPDATED_AMOUNT_PAID
        );
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByAmountPaidIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where amountPaid is less than or equal to
        defaultDebtPaymentFiltering(
            "amountPaid.lessThanOrEqual=" + DEFAULT_AMOUNT_PAID,
            "amountPaid.lessThanOrEqual=" + SMALLER_AMOUNT_PAID
        );
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByAmountPaidIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where amountPaid is less than
        defaultDebtPaymentFiltering("amountPaid.lessThan=" + UPDATED_AMOUNT_PAID, "amountPaid.lessThan=" + DEFAULT_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByAmountPaidIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where amountPaid is greater than
        defaultDebtPaymentFiltering("amountPaid.greaterThan=" + SMALLER_AMOUNT_PAID, "amountPaid.greaterThan=" + DEFAULT_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByRemainingAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where remainingAmount equals to
        defaultDebtPaymentFiltering(
            "remainingAmount.equals=" + DEFAULT_REMAINING_AMOUNT,
            "remainingAmount.equals=" + UPDATED_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByRemainingAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where remainingAmount in
        defaultDebtPaymentFiltering(
            "remainingAmount.in=" + DEFAULT_REMAINING_AMOUNT + "," + UPDATED_REMAINING_AMOUNT,
            "remainingAmount.in=" + UPDATED_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByRemainingAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where remainingAmount is not null
        defaultDebtPaymentFiltering("remainingAmount.specified=true", "remainingAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByRemainingAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where remainingAmount is greater than or equal to
        defaultDebtPaymentFiltering(
            "remainingAmount.greaterThanOrEqual=" + DEFAULT_REMAINING_AMOUNT,
            "remainingAmount.greaterThanOrEqual=" + UPDATED_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByRemainingAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where remainingAmount is less than or equal to
        defaultDebtPaymentFiltering(
            "remainingAmount.lessThanOrEqual=" + DEFAULT_REMAINING_AMOUNT,
            "remainingAmount.lessThanOrEqual=" + SMALLER_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByRemainingAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where remainingAmount is less than
        defaultDebtPaymentFiltering(
            "remainingAmount.lessThan=" + UPDATED_REMAINING_AMOUNT,
            "remainingAmount.lessThan=" + DEFAULT_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByRemainingAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where remainingAmount is greater than
        defaultDebtPaymentFiltering(
            "remainingAmount.greaterThan=" + SMALLER_REMAINING_AMOUNT,
            "remainingAmount.greaterThan=" + DEFAULT_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where date equals to
        defaultDebtPaymentFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where date in
        defaultDebtPaymentFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        // Get all the debtPaymentList where date is not null
        defaultDebtPaymentFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllDebtPaymentsByDebtIsEqualToSomething() throws Exception {
        Debt debt;
        if (TestUtil.findAll(em, Debt.class).isEmpty()) {
            debtPaymentRepository.saveAndFlush(debtPayment);
            debt = DebtResourceIT.createEntity();
        } else {
            debt = TestUtil.findAll(em, Debt.class).get(0);
        }
        em.persist(debt);
        em.flush();
        debtPayment.setDebt(debt);
        debtPaymentRepository.saveAndFlush(debtPayment);
        Long debtId = debt.getId();
        // Get all the debtPaymentList where debt equals to debtId
        defaultDebtPaymentShouldBeFound("debtId.equals=" + debtId);

        // Get all the debtPaymentList where debt equals to (debtId + 1)
        defaultDebtPaymentShouldNotBeFound("debtId.equals=" + (debtId + 1));
    }

    private void defaultDebtPaymentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDebtPaymentShouldBeFound(shouldBeFound);
        defaultDebtPaymentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDebtPaymentShouldBeFound(String filter) throws Exception {
        restDebtPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(debtPayment.getId().intValue())))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID))))
            .andExpect(jsonPath("$.[*].remainingAmount").value(hasItem(sameNumber(DEFAULT_REMAINING_AMOUNT))))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));

        // Check, that the count call also returns 1
        restDebtPaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDebtPaymentShouldNotBeFound(String filter) throws Exception {
        restDebtPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDebtPaymentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDebtPayment() throws Exception {
        // Get the debtPayment
        restDebtPaymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDebtPayment() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the debtPayment
        DebtPayment updatedDebtPayment = debtPaymentRepository.findById(debtPayment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDebtPayment are not directly saved in db
        em.detach(updatedDebtPayment);
        updatedDebtPayment.amountPaid(UPDATED_AMOUNT_PAID).remainingAmount(UPDATED_REMAINING_AMOUNT).date(UPDATED_DATE);
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(updatedDebtPayment);

        restDebtPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, debtPaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(debtPaymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the DebtPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDebtPaymentToMatchAllProperties(updatedDebtPayment);
    }

    @Test
    @Transactional
    void putNonExistingDebtPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debtPayment.setId(longCount.incrementAndGet());

        // Create the DebtPayment
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(debtPayment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDebtPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, debtPaymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(debtPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DebtPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDebtPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debtPayment.setId(longCount.incrementAndGet());

        // Create the DebtPayment
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(debtPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDebtPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(debtPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DebtPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDebtPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debtPayment.setId(longCount.incrementAndGet());

        // Create the DebtPayment
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(debtPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDebtPaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(debtPaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DebtPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDebtPaymentWithPatch() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the debtPayment using partial update
        DebtPayment partialUpdatedDebtPayment = new DebtPayment();
        partialUpdatedDebtPayment.setId(debtPayment.getId());

        restDebtPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDebtPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDebtPayment))
            )
            .andExpect(status().isOk());

        // Validate the DebtPayment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDebtPaymentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDebtPayment, debtPayment),
            getPersistedDebtPayment(debtPayment)
        );
    }

    @Test
    @Transactional
    void fullUpdateDebtPaymentWithPatch() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the debtPayment using partial update
        DebtPayment partialUpdatedDebtPayment = new DebtPayment();
        partialUpdatedDebtPayment.setId(debtPayment.getId());

        partialUpdatedDebtPayment.amountPaid(UPDATED_AMOUNT_PAID).remainingAmount(UPDATED_REMAINING_AMOUNT).date(UPDATED_DATE);

        restDebtPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDebtPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDebtPayment))
            )
            .andExpect(status().isOk());

        // Validate the DebtPayment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDebtPaymentUpdatableFieldsEquals(partialUpdatedDebtPayment, getPersistedDebtPayment(partialUpdatedDebtPayment));
    }

    @Test
    @Transactional
    void patchNonExistingDebtPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debtPayment.setId(longCount.incrementAndGet());

        // Create the DebtPayment
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(debtPayment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDebtPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, debtPaymentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(debtPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DebtPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDebtPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debtPayment.setId(longCount.incrementAndGet());

        // Create the DebtPayment
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(debtPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDebtPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(debtPaymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DebtPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDebtPayment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        debtPayment.setId(longCount.incrementAndGet());

        // Create the DebtPayment
        DebtPaymentDTO debtPaymentDTO = debtPaymentMapper.toDto(debtPayment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDebtPaymentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(debtPaymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DebtPayment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDebtPayment() throws Exception {
        // Initialize the database
        insertedDebtPayment = debtPaymentRepository.saveAndFlush(debtPayment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the debtPayment
        restDebtPaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, debtPayment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return debtPaymentRepository.count();
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

    protected DebtPayment getPersistedDebtPayment(DebtPayment debtPayment) {
        return debtPaymentRepository.findById(debtPayment.getId()).orElseThrow();
    }

    protected void assertPersistedDebtPaymentToMatchAllProperties(DebtPayment expectedDebtPayment) {
        assertDebtPaymentAllPropertiesEquals(expectedDebtPayment, getPersistedDebtPayment(expectedDebtPayment));
    }

    protected void assertPersistedDebtPaymentToMatchUpdatableProperties(DebtPayment expectedDebtPayment) {
        assertDebtPaymentAllUpdatablePropertiesEquals(expectedDebtPayment, getPersistedDebtPayment(expectedDebtPayment));
    }
}
