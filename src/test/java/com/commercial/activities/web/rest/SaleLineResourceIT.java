package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.SaleLineAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static com.commercial.activities.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.Product;
import com.commercial.activities.domain.Sale;
import com.commercial.activities.domain.SaleLine;
import com.commercial.activities.repository.SaleLineRepository;
import com.commercial.activities.service.dto.SaleLineDTO;
import com.commercial.activities.service.mapper.SaleLineMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link SaleLineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SaleLineResourceIT {

    private static final String DEFAULT_BARCODE = "AAAAAAAAAA";
    private static final String UPDATED_BARCODE = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_TOTAL_PRICE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/sale-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SaleLineRepository saleLineRepository;

    @Autowired
    private SaleLineMapper saleLineMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleLineMockMvc;

    private SaleLine saleLine;

    private SaleLine insertedSaleLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleLine createEntity() {
        return new SaleLine()
            .barcode(DEFAULT_BARCODE)
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .totalPrice(DEFAULT_TOTAL_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleLine createUpdatedEntity() {
        return new SaleLine()
            .barcode(UPDATED_BARCODE)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .totalPrice(UPDATED_TOTAL_PRICE);
    }

    @BeforeEach
    void initTest() {
        saleLine = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSaleLine != null) {
            saleLineRepository.delete(insertedSaleLine);
            insertedSaleLine = null;
        }
    }

    @Test
    @Transactional
    void createSaleLine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SaleLine
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(saleLine);
        var returnedSaleLineDTO = om.readValue(
            restSaleLineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleLineDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SaleLineDTO.class
        );

        // Validate the SaleLine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSaleLine = saleLineMapper.toEntity(returnedSaleLineDTO);
        assertSaleLineUpdatableFieldsEquals(returnedSaleLine, getPersistedSaleLine(returnedSaleLine));

        insertedSaleLine = returnedSaleLine;
    }

    @Test
    @Transactional
    void createSaleLineWithExistingId() throws Exception {
        // Create the SaleLine with an existing ID
        saleLine.setId(1L);
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(saleLine);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleLineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SaleLine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleLine.setQuantity(null);

        // Create the SaleLine, which fails.
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(saleLine);

        restSaleLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        saleLine.setUnitPrice(null);

        // Create the SaleLine, which fails.
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(saleLine);

        restSaleLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSaleLines() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList
        restSaleLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].barcode").value(hasItem(DEFAULT_BARCODE)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE))));
    }

    @Test
    @Transactional
    void getSaleLine() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get the saleLine
        restSaleLineMockMvc
            .perform(get(ENTITY_API_URL_ID, saleLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(saleLine.getId().intValue()))
            .andExpect(jsonPath("$.barcode").value(DEFAULT_BARCODE))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.totalPrice").value(sameNumber(DEFAULT_TOTAL_PRICE)));
    }

    @Test
    @Transactional
    void getSaleLinesByIdFiltering() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        Long id = saleLine.getId();

        defaultSaleLineFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSaleLineFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSaleLineFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSaleLinesByBarcodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where barcode equals to
        defaultSaleLineFiltering("barcode.equals=" + DEFAULT_BARCODE, "barcode.equals=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByBarcodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where barcode in
        defaultSaleLineFiltering("barcode.in=" + DEFAULT_BARCODE + "," + UPDATED_BARCODE, "barcode.in=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByBarcodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where barcode is not null
        defaultSaleLineFiltering("barcode.specified=true", "barcode.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleLinesByBarcodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where barcode contains
        defaultSaleLineFiltering("barcode.contains=" + DEFAULT_BARCODE, "barcode.contains=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByBarcodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where barcode does not contain
        defaultSaleLineFiltering("barcode.doesNotContain=" + UPDATED_BARCODE, "barcode.doesNotContain=" + DEFAULT_BARCODE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where quantity equals to
        defaultSaleLineFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleLinesByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where quantity in
        defaultSaleLineFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleLinesByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where quantity is not null
        defaultSaleLineFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleLinesByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where quantity is greater than or equal to
        defaultSaleLineFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleLinesByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where quantity is less than or equal to
        defaultSaleLineFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleLinesByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where quantity is less than
        defaultSaleLineFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleLinesByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where quantity is greater than
        defaultSaleLineFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSaleLinesByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where unitPrice equals to
        defaultSaleLineFiltering("unitPrice.equals=" + DEFAULT_UNIT_PRICE, "unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where unitPrice in
        defaultSaleLineFiltering("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE, "unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where unitPrice is not null
        defaultSaleLineFiltering("unitPrice.specified=true", "unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleLinesByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where unitPrice is greater than or equal to
        defaultSaleLineFiltering(
            "unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE,
            "unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSaleLinesByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where unitPrice is less than or equal to
        defaultSaleLineFiltering("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE, "unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where unitPrice is less than
        defaultSaleLineFiltering("unitPrice.lessThan=" + UPDATED_UNIT_PRICE, "unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where unitPrice is greater than
        defaultSaleLineFiltering("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE, "unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByTotalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where totalPrice equals to
        defaultSaleLineFiltering("totalPrice.equals=" + DEFAULT_TOTAL_PRICE, "totalPrice.equals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByTotalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where totalPrice in
        defaultSaleLineFiltering(
            "totalPrice.in=" + DEFAULT_TOTAL_PRICE + "," + UPDATED_TOTAL_PRICE,
            "totalPrice.in=" + UPDATED_TOTAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSaleLinesByTotalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where totalPrice is not null
        defaultSaleLineFiltering("totalPrice.specified=true", "totalPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllSaleLinesByTotalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where totalPrice is greater than or equal to
        defaultSaleLineFiltering(
            "totalPrice.greaterThanOrEqual=" + DEFAULT_TOTAL_PRICE,
            "totalPrice.greaterThanOrEqual=" + UPDATED_TOTAL_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSaleLinesByTotalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where totalPrice is less than or equal to
        defaultSaleLineFiltering("totalPrice.lessThanOrEqual=" + DEFAULT_TOTAL_PRICE, "totalPrice.lessThanOrEqual=" + SMALLER_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByTotalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where totalPrice is less than
        defaultSaleLineFiltering("totalPrice.lessThan=" + UPDATED_TOTAL_PRICE, "totalPrice.lessThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleLinesByTotalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        // Get all the saleLineList where totalPrice is greater than
        defaultSaleLineFiltering("totalPrice.greaterThan=" + SMALLER_TOTAL_PRICE, "totalPrice.greaterThan=" + DEFAULT_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllSaleLinesBySaleIsEqualToSomething() throws Exception {
        Sale sale;
        if (TestUtil.findAll(em, Sale.class).isEmpty()) {
            saleLineRepository.saveAndFlush(saleLine);
            sale = SaleResourceIT.createEntity();
        } else {
            sale = TestUtil.findAll(em, Sale.class).get(0);
        }
        em.persist(sale);
        em.flush();
        saleLine.setSale(sale);
        saleLineRepository.saveAndFlush(saleLine);
        Long saleId = sale.getId();
        // Get all the saleLineList where sale equals to saleId
        defaultSaleLineShouldBeFound("saleId.equals=" + saleId);

        // Get all the saleLineList where sale equals to (saleId + 1)
        defaultSaleLineShouldNotBeFound("saleId.equals=" + (saleId + 1));
    }

    @Test
    @Transactional
    void getAllSaleLinesByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            saleLineRepository.saveAndFlush(saleLine);
            product = ProductResourceIT.createEntity();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        saleLine.setProduct(product);
        saleLineRepository.saveAndFlush(saleLine);
        Long productId = product.getId();
        // Get all the saleLineList where product equals to productId
        defaultSaleLineShouldBeFound("productId.equals=" + productId);

        // Get all the saleLineList where product equals to (productId + 1)
        defaultSaleLineShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultSaleLineFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSaleLineShouldBeFound(shouldBeFound);
        defaultSaleLineShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSaleLineShouldBeFound(String filter) throws Exception {
        restSaleLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].barcode").value(hasItem(DEFAULT_BARCODE)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE))));

        // Check, that the count call also returns 1
        restSaleLineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSaleLineShouldNotBeFound(String filter) throws Exception {
        restSaleLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSaleLineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSaleLine() throws Exception {
        // Get the saleLine
        restSaleLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSaleLine() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleLine
        SaleLine updatedSaleLine = saleLineRepository.findById(saleLine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSaleLine are not directly saved in db
        em.detach(updatedSaleLine);
        updatedSaleLine.barcode(UPDATED_BARCODE).quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).totalPrice(UPDATED_TOTAL_PRICE);
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(updatedSaleLine);

        restSaleLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleLineDTO))
            )
            .andExpect(status().isOk());

        // Validate the SaleLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSaleLineToMatchAllProperties(updatedSaleLine);
    }

    @Test
    @Transactional
    void putNonExistingSaleLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleLine.setId(longCount.incrementAndGet());

        // Create the SaleLine
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(saleLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSaleLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleLine.setId(longCount.incrementAndGet());

        // Create the SaleLine
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(saleLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSaleLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleLine.setId(longCount.incrementAndGet());

        // Create the SaleLine
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(saleLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSaleLineWithPatch() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleLine using partial update
        SaleLine partialUpdatedSaleLine = new SaleLine();
        partialUpdatedSaleLine.setId(saleLine.getId());

        partialUpdatedSaleLine.quantity(UPDATED_QUANTITY).totalPrice(UPDATED_TOTAL_PRICE);

        restSaleLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleLine))
            )
            .andExpect(status().isOk());

        // Validate the SaleLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleLineUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSaleLine, saleLine), getPersistedSaleLine(saleLine));
    }

    @Test
    @Transactional
    void fullUpdateSaleLineWithPatch() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the saleLine using partial update
        SaleLine partialUpdatedSaleLine = new SaleLine();
        partialUpdatedSaleLine.setId(saleLine.getId());

        partialUpdatedSaleLine
            .barcode(UPDATED_BARCODE)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .totalPrice(UPDATED_TOTAL_PRICE);

        restSaleLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSaleLine))
            )
            .andExpect(status().isOk());

        // Validate the SaleLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleLineUpdatableFieldsEquals(partialUpdatedSaleLine, getPersistedSaleLine(partialUpdatedSaleLine));
    }

    @Test
    @Transactional
    void patchNonExistingSaleLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleLine.setId(longCount.incrementAndGet());

        // Create the SaleLine
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(saleLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleLineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSaleLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleLine.setId(longCount.incrementAndGet());

        // Create the SaleLine
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(saleLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSaleLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        saleLine.setId(longCount.incrementAndGet());

        // Create the SaleLine
        SaleLineDTO saleLineDTO = saleLineMapper.toDto(saleLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleLineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSaleLine() throws Exception {
        // Initialize the database
        insertedSaleLine = saleLineRepository.saveAndFlush(saleLine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the saleLine
        restSaleLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, saleLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return saleLineRepository.count();
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

    protected SaleLine getPersistedSaleLine(SaleLine saleLine) {
        return saleLineRepository.findById(saleLine.getId()).orElseThrow();
    }

    protected void assertPersistedSaleLineToMatchAllProperties(SaleLine expectedSaleLine) {
        assertSaleLineAllPropertiesEquals(expectedSaleLine, getPersistedSaleLine(expectedSaleLine));
    }

    protected void assertPersistedSaleLineToMatchUpdatableProperties(SaleLine expectedSaleLine) {
        assertSaleLineAllUpdatablePropertiesEquals(expectedSaleLine, getPersistedSaleLine(expectedSaleLine));
    }
}
