package com.commercial.activities.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "barcode", nullable = false, unique = true)
    private String barcode;

    @NotNull
    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "initial_stock")
    private Integer initialStock;

    @Column(name = "current_stock")
    private Integer currentStock;

    @NotNull
    @Column(name = "unit_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    private Supplier supplier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public Product barcode(String barcode) {
        this.setBarcode(barcode);
        return this;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDesignation() {
        return this.designation;
    }

    public Product designation(String designation) {
        this.setDesignation(designation);
        return this;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getInitialStock() {
        return this.initialStock;
    }

    public Product initialStock(Integer initialStock) {
        this.setInitialStock(initialStock);
        return this;
    }

    public void setInitialStock(Integer initialStock) {
        this.initialStock = initialStock;
    }

    public Integer getCurrentStock() {
        return this.currentStock;
    }

    public Product currentStock(Integer currentStock) {
        this.setCurrentStock(currentStock);
        return this;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public Product unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Product company(Company company) {
        this.setCompany(company);
        return this;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Product supplier(Supplier supplier) {
        this.setSupplier(supplier);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return getId() != null && getId().equals(((Product) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", barcode='" + getBarcode() + "'" +
            ", designation='" + getDesignation() + "'" +
            ", initialStock=" + getInitialStock() +
            ", currentStock=" + getCurrentStock() +
            ", unitPrice=" + getUnitPrice() +
            "}";
    }
}
