package com.commercial.activities.domain;

import static com.commercial.activities.domain.CompanyTestSamples.*;
import static com.commercial.activities.domain.ProductTestSamples.*;
import static com.commercial.activities.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void companyTest() {
        Product product = getProductRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        product.setCompany(companyBack);
        assertThat(product.getCompany()).isEqualTo(companyBack);

        product.company(null);
        assertThat(product.getCompany()).isNull();
    }

    @Test
    void supplierTest() {
        Product product = getProductRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        product.setSupplier(supplierBack);
        assertThat(product.getSupplier()).isEqualTo(supplierBack);

        product.supplier(null);
        assertThat(product.getSupplier()).isNull();
    }
}
