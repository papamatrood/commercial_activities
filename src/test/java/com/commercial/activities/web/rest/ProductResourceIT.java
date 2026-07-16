package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.ProductAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static com.commercial.activities.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.Product;
import com.commercial.activities.domain.Supplier;
import com.commercial.activities.repository.ProductRepository;
import com.commercial.activities.service.dto.ProductDTO;
import com.commercial.activities.service.mapper.ProductMapper;
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
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_BARCODE = "AAAAAAAAAA";
    private static final String UPDATED_BARCODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESIGNATION = "AAAAAAAAAA";
    private static final String UPDATED_DESIGNATION = "BBBBBBBBBB";

    private static final Integer DEFAULT_INITIAL_STOCK = 1;
    private static final Integer UPDATED_INITIAL_STOCK = 2;
    private static final Integer SMALLER_INITIAL_STOCK = 1 - 1;

    private static final Integer DEFAULT_CURRENT_STOCK = 1;
    private static final Integer UPDATED_CURRENT_STOCK = 2;
    private static final Integer SMALLER_CURRENT_STOCK = 1 - 1;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_UNIT_PRICE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    private Product insertedProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity() {
        return new Product()
            .barcode(DEFAULT_BARCODE)
            .designation(DEFAULT_DESIGNATION)
            .initialStock(DEFAULT_INITIAL_STOCK)
            .currentStock(DEFAULT_CURRENT_STOCK)
            .unitPrice(DEFAULT_UNIT_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity() {
        return new Product()
            .barcode(UPDATED_BARCODE)
            .designation(UPDATED_DESIGNATION)
            .initialStock(UPDATED_INITIAL_STOCK)
            .currentStock(UPDATED_CURRENT_STOCK)
            .unitPrice(UPDATED_UNIT_PRICE);
    }

    @BeforeEach
    void initTest() {
        product = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProduct != null) {
            productRepository.delete(insertedProduct);
            insertedProduct = null;
        }
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        var returnedProductDTO = om.readValue(
            restProductMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductDTO.class
        );

        // Validate the Product in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProduct = productMapper.toEntity(returnedProductDTO);
        assertProductUpdatableFieldsEquals(returnedProduct, getPersistedProduct(returnedProduct));

        insertedProduct = returnedProduct;
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBarcodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setBarcode(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDesignationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setDesignation(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        product.setUnitPrice(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].barcode").value(hasItem(DEFAULT_BARCODE)))
            .andExpect(jsonPath("$.[*].designation").value(hasItem(DEFAULT_DESIGNATION)))
            .andExpect(jsonPath("$.[*].initialStock").value(hasItem(DEFAULT_INITIAL_STOCK)))
            .andExpect(jsonPath("$.[*].currentStock").value(hasItem(DEFAULT_CURRENT_STOCK)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))));
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.barcode").value(DEFAULT_BARCODE))
            .andExpect(jsonPath("$.designation").value(DEFAULT_DESIGNATION))
            .andExpect(jsonPath("$.initialStock").value(DEFAULT_INITIAL_STOCK))
            .andExpect(jsonPath("$.currentStock").value(DEFAULT_CURRENT_STOCK))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        Long id = product.getId();

        defaultProductFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProductFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProductFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByBarcodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where barcode equals to
        defaultProductFiltering("barcode.equals=" + DEFAULT_BARCODE, "barcode.equals=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllProductsByBarcodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where barcode in
        defaultProductFiltering("barcode.in=" + DEFAULT_BARCODE + "," + UPDATED_BARCODE, "barcode.in=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllProductsByBarcodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where barcode is not null
        defaultProductFiltering("barcode.specified=true", "barcode.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByBarcodeContainsSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where barcode contains
        defaultProductFiltering("barcode.contains=" + DEFAULT_BARCODE, "barcode.contains=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllProductsByBarcodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where barcode does not contain
        defaultProductFiltering("barcode.doesNotContain=" + UPDATED_BARCODE, "barcode.doesNotContain=" + DEFAULT_BARCODE);
    }

    @Test
    @Transactional
    void getAllProductsByDesignationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where designation equals to
        defaultProductFiltering("designation.equals=" + DEFAULT_DESIGNATION, "designation.equals=" + UPDATED_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllProductsByDesignationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where designation in
        defaultProductFiltering(
            "designation.in=" + DEFAULT_DESIGNATION + "," + UPDATED_DESIGNATION,
            "designation.in=" + UPDATED_DESIGNATION
        );
    }

    @Test
    @Transactional
    void getAllProductsByDesignationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where designation is not null
        defaultProductFiltering("designation.specified=true", "designation.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDesignationContainsSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where designation contains
        defaultProductFiltering("designation.contains=" + DEFAULT_DESIGNATION, "designation.contains=" + UPDATED_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllProductsByDesignationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where designation does not contain
        defaultProductFiltering("designation.doesNotContain=" + UPDATED_DESIGNATION, "designation.doesNotContain=" + DEFAULT_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllProductsByInitialStockIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where initialStock equals to
        defaultProductFiltering("initialStock.equals=" + DEFAULT_INITIAL_STOCK, "initialStock.equals=" + UPDATED_INITIAL_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByInitialStockIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where initialStock in
        defaultProductFiltering(
            "initialStock.in=" + DEFAULT_INITIAL_STOCK + "," + UPDATED_INITIAL_STOCK,
            "initialStock.in=" + UPDATED_INITIAL_STOCK
        );
    }

    @Test
    @Transactional
    void getAllProductsByInitialStockIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where initialStock is not null
        defaultProductFiltering("initialStock.specified=true", "initialStock.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByInitialStockIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where initialStock is greater than or equal to
        defaultProductFiltering(
            "initialStock.greaterThanOrEqual=" + DEFAULT_INITIAL_STOCK,
            "initialStock.greaterThanOrEqual=" + UPDATED_INITIAL_STOCK
        );
    }

    @Test
    @Transactional
    void getAllProductsByInitialStockIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where initialStock is less than or equal to
        defaultProductFiltering(
            "initialStock.lessThanOrEqual=" + DEFAULT_INITIAL_STOCK,
            "initialStock.lessThanOrEqual=" + SMALLER_INITIAL_STOCK
        );
    }

    @Test
    @Transactional
    void getAllProductsByInitialStockIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where initialStock is less than
        defaultProductFiltering("initialStock.lessThan=" + UPDATED_INITIAL_STOCK, "initialStock.lessThan=" + DEFAULT_INITIAL_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByInitialStockIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where initialStock is greater than
        defaultProductFiltering("initialStock.greaterThan=" + SMALLER_INITIAL_STOCK, "initialStock.greaterThan=" + DEFAULT_INITIAL_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByCurrentStockIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where currentStock equals to
        defaultProductFiltering("currentStock.equals=" + DEFAULT_CURRENT_STOCK, "currentStock.equals=" + UPDATED_CURRENT_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByCurrentStockIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where currentStock in
        defaultProductFiltering(
            "currentStock.in=" + DEFAULT_CURRENT_STOCK + "," + UPDATED_CURRENT_STOCK,
            "currentStock.in=" + UPDATED_CURRENT_STOCK
        );
    }

    @Test
    @Transactional
    void getAllProductsByCurrentStockIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where currentStock is not null
        defaultProductFiltering("currentStock.specified=true", "currentStock.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByCurrentStockIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where currentStock is greater than or equal to
        defaultProductFiltering(
            "currentStock.greaterThanOrEqual=" + DEFAULT_CURRENT_STOCK,
            "currentStock.greaterThanOrEqual=" + UPDATED_CURRENT_STOCK
        );
    }

    @Test
    @Transactional
    void getAllProductsByCurrentStockIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where currentStock is less than or equal to
        defaultProductFiltering(
            "currentStock.lessThanOrEqual=" + DEFAULT_CURRENT_STOCK,
            "currentStock.lessThanOrEqual=" + SMALLER_CURRENT_STOCK
        );
    }

    @Test
    @Transactional
    void getAllProductsByCurrentStockIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where currentStock is less than
        defaultProductFiltering("currentStock.lessThan=" + UPDATED_CURRENT_STOCK, "currentStock.lessThan=" + DEFAULT_CURRENT_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByCurrentStockIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where currentStock is greater than
        defaultProductFiltering("currentStock.greaterThan=" + SMALLER_CURRENT_STOCK, "currentStock.greaterThan=" + DEFAULT_CURRENT_STOCK);
    }

    @Test
    @Transactional
    void getAllProductsByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitPrice equals to
        defaultProductFiltering("unitPrice.equals=" + DEFAULT_UNIT_PRICE, "unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitPrice in
        defaultProductFiltering("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE, "unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitPrice is not null
        defaultProductFiltering("unitPrice.specified=true", "unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitPrice is greater than or equal to
        defaultProductFiltering("unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE, "unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitPrice is less than or equal to
        defaultProductFiltering("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE, "unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitPrice is less than
        defaultProductFiltering("unitPrice.lessThan=" + UPDATED_UNIT_PRICE, "unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        // Get all the productList where unitPrice is greater than
        defaultProductFiltering("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE, "unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByCompanyIsEqualToSomething() throws Exception {
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            productRepository.saveAndFlush(product);
            company = CompanyResourceIT.createEntity();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        em.persist(company);
        em.flush();
        product.setCompany(company);
        productRepository.saveAndFlush(product);
        Long companyId = company.getId();
        // Get all the productList where company equals to companyId
        defaultProductShouldBeFound("companyId.equals=" + companyId);

        // Get all the productList where company equals to (companyId + 1)
        defaultProductShouldNotBeFound("companyId.equals=" + (companyId + 1));
    }

    @Test
    @Transactional
    void getAllProductsBySupplierIsEqualToSomething() throws Exception {
        Supplier supplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            productRepository.saveAndFlush(product);
            supplier = SupplierResourceIT.createEntity();
        } else {
            supplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        em.persist(supplier);
        em.flush();
        product.setSupplier(supplier);
        productRepository.saveAndFlush(product);
        Long supplierId = supplier.getId();
        // Get all the productList where supplier equals to supplierId
        defaultProductShouldBeFound("supplierId.equals=" + supplierId);

        // Get all the productList where supplier equals to (supplierId + 1)
        defaultProductShouldNotBeFound("supplierId.equals=" + (supplierId + 1));
    }

    private void defaultProductFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProductShouldBeFound(shouldBeFound);
        defaultProductShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].barcode").value(hasItem(DEFAULT_BARCODE)))
            .andExpect(jsonPath("$.[*].designation").value(hasItem(DEFAULT_DESIGNATION)))
            .andExpect(jsonPath("$.[*].initialStock").value(hasItem(DEFAULT_INITIAL_STOCK)))
            .andExpect(jsonPath("$.[*].currentStock").value(hasItem(DEFAULT_CURRENT_STOCK)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))));

        // Check, that the count call also returns 1
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProduct() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .barcode(UPDATED_BARCODE)
            .designation(UPDATED_DESIGNATION)
            .initialStock(UPDATED_INITIAL_STOCK)
            .currentStock(UPDATED_CURRENT_STOCK)
            .unitPrice(UPDATED_UNIT_PRICE);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductToMatchAllProperties(updatedProduct);
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct.barcode(UPDATED_BARCODE).unitPrice(UPDATED_UNIT_PRICE);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProduct, product), getPersistedProduct(product));
    }

    @Test
    @Transactional
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .barcode(UPDATED_BARCODE)
            .designation(UPDATED_DESIGNATION)
            .initialStock(UPDATED_INITIAL_STOCK)
            .currentStock(UPDATED_CURRENT_STOCK)
            .unitPrice(UPDATED_UNIT_PRICE);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductUpdatableFieldsEquals(partialUpdatedProduct, getPersistedProduct(partialUpdatedProduct));
    }

    @Test
    @Transactional
    void patchNonExistingProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        product.setId(longCount.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        // Initialize the database
        insertedProduct = productRepository.saveAndFlush(product);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the product
        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productRepository.count();
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

    protected Product getPersistedProduct(Product product) {
        return productRepository.findById(product.getId()).orElseThrow();
    }

    protected void assertPersistedProductToMatchAllProperties(Product expectedProduct) {
        assertProductAllPropertiesEquals(expectedProduct, getPersistedProduct(expectedProduct));
    }

    protected void assertPersistedProductToMatchUpdatableProperties(Product expectedProduct) {
        assertProductAllUpdatablePropertiesEquals(expectedProduct, getPersistedProduct(expectedProduct));
    }
}
