package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.SaleAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static com.commercial.activities.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.Sale;
import com.commercial.activities.repository.SaleRepository;
import com.commercial.activities.service.dto.SaleDTO;
import com.commercial.activities.service.mapper.SaleMapper;
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
 * Integration tests for the {@link SaleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SaleResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.ofEpochMilli(1784236791515L);

    private static final BigDecimal DEFAULT_AMOUNT_TO_PAY = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_TO_PAY = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT_TO_PAY = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_AMOUNT_PAID = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_PAID = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT_PAID = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_REMAINING_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_REMAINING_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_REMAINING_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_CUSTOMER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_COMPANY = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER_CONTACT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sales";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleMapper saleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleMockMvc;

    private Sale sale;

    private Sale insertedSale;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sale createEntity() {
        return new Sale()
            .date(DEFAULT_DATE)
            .amountToPay(DEFAULT_AMOUNT_TO_PAY)
            .amountPaid(DEFAULT_AMOUNT_PAID)
            .remainingAmount(DEFAULT_REMAINING_AMOUNT)
            .customerName(DEFAULT_CUSTOMER_NAME)
            .customerCompany(DEFAULT_CUSTOMER_COMPANY)
            .customerContact(DEFAULT_CUSTOMER_CONTACT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sale createUpdatedEntity() {
        return new Sale()
            .date(UPDATED_DATE)
            .amountToPay(UPDATED_AMOUNT_TO_PAY)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .remainingAmount(UPDATED_REMAINING_AMOUNT)
            .customerName(UPDATED_CUSTOMER_NAME)
            .customerCompany(UPDATED_CUSTOMER_COMPANY)
            .customerContact(UPDATED_CUSTOMER_CONTACT);
    }

    @BeforeEach
    void initTest() {
        sale = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSale != null) {
            saleRepository.delete(insertedSale);
            insertedSale = null;
        }
    }

    @Test
    @Transactional
    void createSale() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);
        var returnedSaleDTO = om.readValue(
            restSaleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SaleDTO.class
        );

        // Validate the Sale in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSale = saleMapper.toEntity(returnedSaleDTO);
        assertSaleUpdatableFieldsEquals(returnedSale, getPersistedSale(returnedSale));

        insertedSale = returnedSale;
    }

    @Test
    @Transactional
    void createSaleWithExistingId() throws Exception {
        // Create the Sale with an existing ID
        sale.setId(1L);
        SaleDTO saleDTO = saleMapper.toDto(sale);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sale.setDate(null);

        // Create the Sale, which fails.
        SaleDTO saleDTO = saleMapper.toDto(sale);

        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountToPayIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sale.setAmountToPay(null);

        // Create the Sale, which fails.
        SaleDTO saleDTO = saleMapper.toDto(sale);

        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountPaidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sale.setAmountPaid(null);

        // Create the Sale, which fails.
        SaleDTO saleDTO = saleMapper.toDto(sale);

        restSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSales() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sale.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amountToPay").value(hasItem(sameNumber(DEFAULT_AMOUNT_TO_PAY))))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID))))
            .andExpect(jsonPath("$.[*].remainingAmount").value(hasItem(sameNumber(DEFAULT_REMAINING_AMOUNT))))
            .andExpect(jsonPath("$.[*].customerName").value(hasItem(DEFAULT_CUSTOMER_NAME)))
            .andExpect(jsonPath("$.[*].customerCompany").value(hasItem(DEFAULT_CUSTOMER_COMPANY)))
            .andExpect(jsonPath("$.[*].customerContact").value(hasItem(DEFAULT_CUSTOMER_CONTACT)));
    }

    @Test
    @Transactional
    void getSale() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get the sale
        restSaleMockMvc
            .perform(get(ENTITY_API_URL_ID, sale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sale.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.amountToPay").value(sameNumber(DEFAULT_AMOUNT_TO_PAY)))
            .andExpect(jsonPath("$.amountPaid").value(sameNumber(DEFAULT_AMOUNT_PAID)))
            .andExpect(jsonPath("$.remainingAmount").value(sameNumber(DEFAULT_REMAINING_AMOUNT)))
            .andExpect(jsonPath("$.customerName").value(DEFAULT_CUSTOMER_NAME))
            .andExpect(jsonPath("$.customerCompany").value(DEFAULT_CUSTOMER_COMPANY))
            .andExpect(jsonPath("$.customerContact").value(DEFAULT_CUSTOMER_CONTACT));
    }

    @Test
    @Transactional
    void getSalesByIdFiltering() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        Long id = sale.getId();

        defaultSaleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSaleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSaleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where date equals to
        defaultSaleFiltering("date.equals=" + DEFAULT_DATE, "date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllSalesByDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where date in
        defaultSaleFiltering("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE, "date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllSalesByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where date is not null
        defaultSaleFiltering("date.specified=true", "date.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByAmountToPayIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountToPay equals to
        defaultSaleFiltering("amountToPay.equals=" + DEFAULT_AMOUNT_TO_PAY, "amountToPay.equals=" + UPDATED_AMOUNT_TO_PAY);
    }

    @Test
    @Transactional
    void getAllSalesByAmountToPayIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountToPay in
        defaultSaleFiltering(
            "amountToPay.in=" + DEFAULT_AMOUNT_TO_PAY + "," + UPDATED_AMOUNT_TO_PAY,
            "amountToPay.in=" + UPDATED_AMOUNT_TO_PAY
        );
    }

    @Test
    @Transactional
    void getAllSalesByAmountToPayIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountToPay is not null
        defaultSaleFiltering("amountToPay.specified=true", "amountToPay.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByAmountToPayIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountToPay is greater than or equal to
        defaultSaleFiltering(
            "amountToPay.greaterThanOrEqual=" + DEFAULT_AMOUNT_TO_PAY,
            "amountToPay.greaterThanOrEqual=" + UPDATED_AMOUNT_TO_PAY
        );
    }

    @Test
    @Transactional
    void getAllSalesByAmountToPayIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountToPay is less than or equal to
        defaultSaleFiltering(
            "amountToPay.lessThanOrEqual=" + DEFAULT_AMOUNT_TO_PAY,
            "amountToPay.lessThanOrEqual=" + SMALLER_AMOUNT_TO_PAY
        );
    }

    @Test
    @Transactional
    void getAllSalesByAmountToPayIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountToPay is less than
        defaultSaleFiltering("amountToPay.lessThan=" + UPDATED_AMOUNT_TO_PAY, "amountToPay.lessThan=" + DEFAULT_AMOUNT_TO_PAY);
    }

    @Test
    @Transactional
    void getAllSalesByAmountToPayIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountToPay is greater than
        defaultSaleFiltering("amountToPay.greaterThan=" + SMALLER_AMOUNT_TO_PAY, "amountToPay.greaterThan=" + DEFAULT_AMOUNT_TO_PAY);
    }

    @Test
    @Transactional
    void getAllSalesByAmountPaidIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountPaid equals to
        defaultSaleFiltering("amountPaid.equals=" + DEFAULT_AMOUNT_PAID, "amountPaid.equals=" + UPDATED_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllSalesByAmountPaidIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountPaid in
        defaultSaleFiltering("amountPaid.in=" + DEFAULT_AMOUNT_PAID + "," + UPDATED_AMOUNT_PAID, "amountPaid.in=" + UPDATED_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllSalesByAmountPaidIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountPaid is not null
        defaultSaleFiltering("amountPaid.specified=true", "amountPaid.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByAmountPaidIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountPaid is greater than or equal to
        defaultSaleFiltering(
            "amountPaid.greaterThanOrEqual=" + DEFAULT_AMOUNT_PAID,
            "amountPaid.greaterThanOrEqual=" + UPDATED_AMOUNT_PAID
        );
    }

    @Test
    @Transactional
    void getAllSalesByAmountPaidIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountPaid is less than or equal to
        defaultSaleFiltering("amountPaid.lessThanOrEqual=" + DEFAULT_AMOUNT_PAID, "amountPaid.lessThanOrEqual=" + SMALLER_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllSalesByAmountPaidIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountPaid is less than
        defaultSaleFiltering("amountPaid.lessThan=" + UPDATED_AMOUNT_PAID, "amountPaid.lessThan=" + DEFAULT_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllSalesByAmountPaidIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where amountPaid is greater than
        defaultSaleFiltering("amountPaid.greaterThan=" + SMALLER_AMOUNT_PAID, "amountPaid.greaterThan=" + DEFAULT_AMOUNT_PAID);
    }

    @Test
    @Transactional
    void getAllSalesByRemainingAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where remainingAmount equals to
        defaultSaleFiltering("remainingAmount.equals=" + DEFAULT_REMAINING_AMOUNT, "remainingAmount.equals=" + UPDATED_REMAINING_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSalesByRemainingAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where remainingAmount in
        defaultSaleFiltering(
            "remainingAmount.in=" + DEFAULT_REMAINING_AMOUNT + "," + UPDATED_REMAINING_AMOUNT,
            "remainingAmount.in=" + UPDATED_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSalesByRemainingAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where remainingAmount is not null
        defaultSaleFiltering("remainingAmount.specified=true", "remainingAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByRemainingAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where remainingAmount is greater than or equal to
        defaultSaleFiltering(
            "remainingAmount.greaterThanOrEqual=" + DEFAULT_REMAINING_AMOUNT,
            "remainingAmount.greaterThanOrEqual=" + UPDATED_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSalesByRemainingAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where remainingAmount is less than or equal to
        defaultSaleFiltering(
            "remainingAmount.lessThanOrEqual=" + DEFAULT_REMAINING_AMOUNT,
            "remainingAmount.lessThanOrEqual=" + SMALLER_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSalesByRemainingAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where remainingAmount is less than
        defaultSaleFiltering(
            "remainingAmount.lessThan=" + UPDATED_REMAINING_AMOUNT,
            "remainingAmount.lessThan=" + DEFAULT_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSalesByRemainingAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where remainingAmount is greater than
        defaultSaleFiltering(
            "remainingAmount.greaterThan=" + SMALLER_REMAINING_AMOUNT,
            "remainingAmount.greaterThan=" + DEFAULT_REMAINING_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllSalesByCustomerNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerName equals to
        defaultSaleFiltering("customerName.equals=" + DEFAULT_CUSTOMER_NAME, "customerName.equals=" + UPDATED_CUSTOMER_NAME);
    }

    @Test
    @Transactional
    void getAllSalesByCustomerNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerName in
        defaultSaleFiltering(
            "customerName.in=" + DEFAULT_CUSTOMER_NAME + "," + UPDATED_CUSTOMER_NAME,
            "customerName.in=" + UPDATED_CUSTOMER_NAME
        );
    }

    @Test
    @Transactional
    void getAllSalesByCustomerNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerName is not null
        defaultSaleFiltering("customerName.specified=true", "customerName.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByCustomerNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerName contains
        defaultSaleFiltering("customerName.contains=" + DEFAULT_CUSTOMER_NAME, "customerName.contains=" + UPDATED_CUSTOMER_NAME);
    }

    @Test
    @Transactional
    void getAllSalesByCustomerNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerName does not contain
        defaultSaleFiltering(
            "customerName.doesNotContain=" + UPDATED_CUSTOMER_NAME,
            "customerName.doesNotContain=" + DEFAULT_CUSTOMER_NAME
        );
    }

    @Test
    @Transactional
    void getAllSalesByCustomerCompanyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerCompany equals to
        defaultSaleFiltering("customerCompany.equals=" + DEFAULT_CUSTOMER_COMPANY, "customerCompany.equals=" + UPDATED_CUSTOMER_COMPANY);
    }

    @Test
    @Transactional
    void getAllSalesByCustomerCompanyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerCompany in
        defaultSaleFiltering(
            "customerCompany.in=" + DEFAULT_CUSTOMER_COMPANY + "," + UPDATED_CUSTOMER_COMPANY,
            "customerCompany.in=" + UPDATED_CUSTOMER_COMPANY
        );
    }

    @Test
    @Transactional
    void getAllSalesByCustomerCompanyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerCompany is not null
        defaultSaleFiltering("customerCompany.specified=true", "customerCompany.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByCustomerCompanyContainsSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerCompany contains
        defaultSaleFiltering(
            "customerCompany.contains=" + DEFAULT_CUSTOMER_COMPANY,
            "customerCompany.contains=" + UPDATED_CUSTOMER_COMPANY
        );
    }

    @Test
    @Transactional
    void getAllSalesByCustomerCompanyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerCompany does not contain
        defaultSaleFiltering(
            "customerCompany.doesNotContain=" + UPDATED_CUSTOMER_COMPANY,
            "customerCompany.doesNotContain=" + DEFAULT_CUSTOMER_COMPANY
        );
    }

    @Test
    @Transactional
    void getAllSalesByCustomerContactIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerContact equals to
        defaultSaleFiltering("customerContact.equals=" + DEFAULT_CUSTOMER_CONTACT, "customerContact.equals=" + UPDATED_CUSTOMER_CONTACT);
    }

    @Test
    @Transactional
    void getAllSalesByCustomerContactIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerContact in
        defaultSaleFiltering(
            "customerContact.in=" + DEFAULT_CUSTOMER_CONTACT + "," + UPDATED_CUSTOMER_CONTACT,
            "customerContact.in=" + UPDATED_CUSTOMER_CONTACT
        );
    }

    @Test
    @Transactional
    void getAllSalesByCustomerContactIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerContact is not null
        defaultSaleFiltering("customerContact.specified=true", "customerContact.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByCustomerContactContainsSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerContact contains
        defaultSaleFiltering(
            "customerContact.contains=" + DEFAULT_CUSTOMER_CONTACT,
            "customerContact.contains=" + UPDATED_CUSTOMER_CONTACT
        );
    }

    @Test
    @Transactional
    void getAllSalesByCustomerContactNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        // Get all the saleList where customerContact does not contain
        defaultSaleFiltering(
            "customerContact.doesNotContain=" + UPDATED_CUSTOMER_CONTACT,
            "customerContact.doesNotContain=" + DEFAULT_CUSTOMER_CONTACT
        );
    }

    @Test
    @Transactional
    void getAllSalesByCompanyIsEqualToSomething() throws Exception {
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            saleRepository.saveAndFlush(sale);
            company = CompanyResourceIT.createEntity();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        em.persist(company);
        em.flush();
        sale.setCompany(company);
        saleRepository.saveAndFlush(sale);
        Long companyId = company.getId();
        // Get all the saleList where company equals to companyId
        defaultSaleShouldBeFound("companyId.equals=" + companyId);

        // Get all the saleList where company equals to (companyId + 1)
        defaultSaleShouldNotBeFound("companyId.equals=" + (companyId + 1));
    }

    @Test
    @Transactional
    void getAllSalesBySellerIsEqualToSomething() throws Exception {
        AppUser seller;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            saleRepository.saveAndFlush(sale);
            seller = AppUserResourceIT.createEntity();
        } else {
            seller = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(seller);
        em.flush();
        sale.setSeller(seller);
        saleRepository.saveAndFlush(sale);
        Long sellerId = seller.getId();
        // Get all the saleList where seller equals to sellerId
        defaultSaleShouldBeFound("sellerId.equals=" + sellerId);

        // Get all the saleList where seller equals to (sellerId + 1)
        defaultSaleShouldNotBeFound("sellerId.equals=" + (sellerId + 1));
    }

    private void defaultSaleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSaleShouldBeFound(shouldBeFound);
        defaultSaleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSaleShouldBeFound(String filter) throws Exception {
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sale.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amountToPay").value(hasItem(sameNumber(DEFAULT_AMOUNT_TO_PAY))))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID))))
            .andExpect(jsonPath("$.[*].remainingAmount").value(hasItem(sameNumber(DEFAULT_REMAINING_AMOUNT))))
            .andExpect(jsonPath("$.[*].customerName").value(hasItem(DEFAULT_CUSTOMER_NAME)))
            .andExpect(jsonPath("$.[*].customerCompany").value(hasItem(DEFAULT_CUSTOMER_COMPANY)))
            .andExpect(jsonPath("$.[*].customerContact").value(hasItem(DEFAULT_CUSTOMER_CONTACT)));

        // Check, that the count call also returns 1
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSaleShouldNotBeFound(String filter) throws Exception {
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSaleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSale() throws Exception {
        // Get the sale
        restSaleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSale() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sale
        Sale updatedSale = saleRepository.findById(sale.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSale are not directly saved in db
        em.detach(updatedSale);
        updatedSale
            .date(UPDATED_DATE)
            .amountToPay(UPDATED_AMOUNT_TO_PAY)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .remainingAmount(UPDATED_REMAINING_AMOUNT)
            .customerName(UPDATED_CUSTOMER_NAME)
            .customerCompany(UPDATED_CUSTOMER_COMPANY)
            .customerContact(UPDATED_CUSTOMER_CONTACT);
        SaleDTO saleDTO = saleMapper.toDto(updatedSale);

        restSaleMockMvc
            .perform(put(ENTITY_API_URL_ID, saleDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isOk());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSaleToMatchAllProperties(updatedSale);
    }

    @Test
    @Transactional
    void putNonExistingSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(put(ENTITY_API_URL_ID, saleDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(saleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSaleWithPatch() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sale using partial update
        Sale partialUpdatedSale = new Sale();
        partialUpdatedSale.setId(sale.getId());

        partialUpdatedSale
            .date(UPDATED_DATE)
            .amountToPay(UPDATED_AMOUNT_TO_PAY)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .remainingAmount(UPDATED_REMAINING_AMOUNT)
            .customerCompany(UPDATED_CUSTOMER_COMPANY);

        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSale.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSale))
            )
            .andExpect(status().isOk());

        // Validate the Sale in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSale, sale), getPersistedSale(sale));
    }

    @Test
    @Transactional
    void fullUpdateSaleWithPatch() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sale using partial update
        Sale partialUpdatedSale = new Sale();
        partialUpdatedSale.setId(sale.getId());

        partialUpdatedSale
            .date(UPDATED_DATE)
            .amountToPay(UPDATED_AMOUNT_TO_PAY)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .remainingAmount(UPDATED_REMAINING_AMOUNT)
            .customerName(UPDATED_CUSTOMER_NAME)
            .customerCompany(UPDATED_CUSTOMER_COMPANY)
            .customerContact(UPDATED_CUSTOMER_CONTACT);

        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSale.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSale))
            )
            .andExpect(status().isOk());

        // Validate the Sale in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSaleUpdatableFieldsEquals(partialUpdatedSale, getPersistedSale(partialUpdatedSale));
    }

    @Test
    @Transactional
    void patchNonExistingSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(saleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sale.setId(longCount.incrementAndGet());

        // Create the Sale
        SaleDTO saleDTO = saleMapper.toDto(sale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(saleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSale() throws Exception {
        // Initialize the database
        insertedSale = saleRepository.saveAndFlush(sale);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sale
        restSaleMockMvc
            .perform(delete(ENTITY_API_URL_ID, sale.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return saleRepository.count();
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

    protected Sale getPersistedSale(Sale sale) {
        return saleRepository.findById(sale.getId()).orElseThrow();
    }

    protected void assertPersistedSaleToMatchAllProperties(Sale expectedSale) {
        assertSaleAllPropertiesEquals(expectedSale, getPersistedSale(expectedSale));
    }

    protected void assertPersistedSaleToMatchUpdatableProperties(Sale expectedSale) {
        assertSaleAllUpdatablePropertiesEquals(expectedSale, getPersistedSale(expectedSale));
    }
}
