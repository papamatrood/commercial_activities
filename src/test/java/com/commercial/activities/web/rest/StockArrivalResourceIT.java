package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.StockArrivalAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static com.commercial.activities.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.Product;
import com.commercial.activities.domain.StockArrival;
import com.commercial.activities.repository.StockArrivalRepository;
import com.commercial.activities.service.dto.StockArrivalDTO;
import com.commercial.activities.service.mapper.StockArrivalMapper;
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
 * Integration tests for the {@link StockArrivalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StockArrivalResourceIT {

    private static final String DEFAULT_BARCODE = "AAAAAAAAAA";
    private static final String UPDATED_BARCODE = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.ofEpochMilli(1784236791515L);

    private static final String ENTITY_API_URL = "/api/stock-arrivals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockArrivalRepository stockArrivalRepository;

    @Autowired
    private StockArrivalMapper stockArrivalMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockArrivalMockMvc;

    private StockArrival stockArrival;

    private StockArrival insertedStockArrival;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockArrival createEntity() {
        return new StockArrival().barcode(DEFAULT_BARCODE).quantity(DEFAULT_QUANTITY).amount(DEFAULT_AMOUNT).date(DEFAULT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockArrival createUpdatedEntity() {
        return new StockArrival().barcode(UPDATED_BARCODE).quantity(UPDATED_QUANTITY).amount(UPDATED_AMOUNT).date(UPDATED_DATE);
    }

    @BeforeEach
    void initTest() {
        stockArrival = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStockArrival != null) {
            stockArrivalRepository.delete(insertedStockArrival);
            insertedStockArrival = null;
        }
    }

    @Test
    @Transactional
    void createStockArrival() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StockArrival
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(stockArrival);
        var returnedStockArrivalDTO = om.readValue(
            restStockArrivalMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockArrivalDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockArrivalDTO.class
        );

        // Validate the StockArrival in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStockArrival = stockArrivalMapper.toEntity(returnedStockArrivalDTO);
        assertStockArrivalUpdatableFieldsEquals(returnedStockArrival, getPersistedStockArrival(returnedStockArrival));

        insertedStockArrival = returnedStockArrival;
    }

    @Test
    @Transactional
    void createStockArrivalWithExistingId() throws Exception {
        // Create the StockArrival with an existing ID
        stockArrival.setId(1L);
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(stockArrival);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockArrivalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockArrivalDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockArrival in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockArrival.setQuantity(null);

        // Create the StockArrival, which fails.
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(stockArrival);

        restStockArrivalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockArrivalDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockArrival.setDate(null);

        // Create the StockArrival, which fails.
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(stockArrival);

        restStockArrivalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockArrivalDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockArrivals() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList
        restStockArrivalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockArrival.getId().intValue())))
            .andExpect(jsonPath("$.[*].barcode").value(hasItem(DEFAULT_BARCODE)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getStockArrival() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get the stockArrival
        restStockArrivalMockMvc
            .perform(get(ENTITY_API_URL_ID, stockArrival.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockArrival.getId().intValue()))
            .andExpect(jsonPath("$.barcode").value(DEFAULT_BARCODE))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getStockArrivalsByIdFiltering() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        Long id = stockArrival.getId();

        defaultStockArrivalFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStockArrivalFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStockArrivalFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByBarcodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where barcode equals to
        defaultStockArrivalFiltering("barcode.equals=" + DEFAULT_BARCODE, "barcode.equals=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByBarcodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where barcode in
        defaultStockArrivalFiltering("barcode.in=" + DEFAULT_BARCODE + "," + UPDATED_BARCODE, "barcode.in=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByBarcodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where barcode is not null
        defaultStockArrivalFiltering("barcode.specified=true", "barcode.specified=false");
    }

    @Test
    @Transactional
    void getAllStockArrivalsByBarcodeContainsSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where barcode contains
        defaultStockArrivalFiltering("barcode.contains=" + DEFAULT_BARCODE, "barcode.contains=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByBarcodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where barcode does not contain
        defaultStockArrivalFiltering("barcode.doesNotContain=" + UPDATED_BARCODE, "barcode.doesNotContain=" + DEFAULT_BARCODE);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where quantity equals to
        defaultStockArrivalFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where quantity in
        defaultStockArrivalFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where quantity is not null
        defaultStockArrivalFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllStockArrivalsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where quantity is greater than or equal to
        defaultStockArrivalFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where quantity is less than or equal to
        defaultStockArrivalFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where quantity is less than
        defaultStockArrivalFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where quantity is greater than
        defaultStockArrivalFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where amount equals to
        defaultStockArrivalFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where amount in
        defaultStockArrivalFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where amount is not null
        defaultStockArrivalFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllStockArrivalsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where amount is greater than or equal to
        defaultStockArrivalFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where amount is less than or equal to
        defaultStockArrivalFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where amount is less than
        defaultStockArrivalFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where amount is greater than
        defaultStockArrivalFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where date equals to
        defaultStockArrivalFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where date in
        defaultStockArrivalFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllStockArrivalsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        // Get all the stockArrivalList where date is not null
        defaultStockArrivalFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllStockArrivalsByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            stockArrivalRepository.saveAndFlush(stockArrival);
            product = ProductResourceIT.createEntity();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        stockArrival.setProduct(product);
        stockArrivalRepository.saveAndFlush(stockArrival);
        Long productId = product.getId();
        // Get all the stockArrivalList where product equals to productId
        defaultStockArrivalShouldBeFound("productId.equals=" + productId);

        // Get all the stockArrivalList where product equals to (productId + 1)
        defaultStockArrivalShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultStockArrivalFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStockArrivalShouldBeFound(shouldBeFound);
        defaultStockArrivalShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockArrivalShouldBeFound(String filter) throws Exception {
        restStockArrivalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockArrival.getId().intValue())))
            .andExpect(jsonPath("$.[*].barcode").value(hasItem(DEFAULT_BARCODE)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));

        // Check, that the count call also returns 1
        restStockArrivalMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockArrivalShouldNotBeFound(String filter) throws Exception {
        restStockArrivalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockArrivalMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockArrival() throws Exception {
        // Get the stockArrival
        restStockArrivalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockArrival() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockArrival
        StockArrival updatedStockArrival = stockArrivalRepository.findById(stockArrival.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockArrival are not directly saved in db
        em.detach(updatedStockArrival);
        updatedStockArrival.barcode(UPDATED_BARCODE).quantity(UPDATED_QUANTITY).amount(UPDATED_AMOUNT).date(UPDATED_DATE);
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(updatedStockArrival);

        restStockArrivalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockArrivalDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockArrivalDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockArrival in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockArrivalToMatchAllProperties(updatedStockArrival);
    }

    @Test
    @Transactional
    void putNonExistingStockArrival() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockArrival.setId(longCount.incrementAndGet());

        // Create the StockArrival
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(stockArrival);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockArrivalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockArrivalDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockArrivalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockArrival in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockArrival() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockArrival.setId(longCount.incrementAndGet());

        // Create the StockArrival
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(stockArrival);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockArrivalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockArrivalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockArrival in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockArrival() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockArrival.setId(longCount.incrementAndGet());

        // Create the StockArrival
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(stockArrival);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockArrivalMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockArrivalDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockArrival in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockArrivalWithPatch() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockArrival using partial update
        StockArrival partialUpdatedStockArrival = new StockArrival();
        partialUpdatedStockArrival.setId(stockArrival.getId());

        partialUpdatedStockArrival.amount(UPDATED_AMOUNT);

        restStockArrivalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockArrival.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockArrival))
            )
            .andExpect(status().isOk());

        // Validate the StockArrival in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockArrivalUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockArrival, stockArrival),
            getPersistedStockArrival(stockArrival)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockArrivalWithPatch() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockArrival using partial update
        StockArrival partialUpdatedStockArrival = new StockArrival();
        partialUpdatedStockArrival.setId(stockArrival.getId());

        partialUpdatedStockArrival.barcode(UPDATED_BARCODE).quantity(UPDATED_QUANTITY).amount(UPDATED_AMOUNT).date(UPDATED_DATE);

        restStockArrivalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockArrival.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockArrival))
            )
            .andExpect(status().isOk());

        // Validate the StockArrival in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockArrivalUpdatableFieldsEquals(partialUpdatedStockArrival, getPersistedStockArrival(partialUpdatedStockArrival));
    }

    @Test
    @Transactional
    void patchNonExistingStockArrival() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockArrival.setId(longCount.incrementAndGet());

        // Create the StockArrival
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(stockArrival);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockArrivalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockArrivalDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockArrivalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockArrival in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockArrival() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockArrival.setId(longCount.incrementAndGet());

        // Create the StockArrival
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(stockArrival);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockArrivalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockArrivalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockArrival in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockArrival() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockArrival.setId(longCount.incrementAndGet());

        // Create the StockArrival
        StockArrivalDTO stockArrivalDTO = stockArrivalMapper.toDto(stockArrival);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockArrivalMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockArrivalDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockArrival in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockArrival() throws Exception {
        // Initialize the database
        insertedStockArrival = stockArrivalRepository.saveAndFlush(stockArrival);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stockArrival
        restStockArrivalMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockArrival.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stockArrivalRepository.count();
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

    protected StockArrival getPersistedStockArrival(StockArrival stockArrival) {
        return stockArrivalRepository.findById(stockArrival.getId()).orElseThrow();
    }

    protected void assertPersistedStockArrivalToMatchAllProperties(StockArrival expectedStockArrival) {
        assertStockArrivalAllPropertiesEquals(expectedStockArrival, getPersistedStockArrival(expectedStockArrival));
    }

    protected void assertPersistedStockArrivalToMatchUpdatableProperties(StockArrival expectedStockArrival) {
        assertStockArrivalAllUpdatablePropertiesEquals(expectedStockArrival, getPersistedStockArrival(expectedStockArrival));
    }
}
