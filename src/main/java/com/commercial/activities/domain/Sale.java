package com.commercial.activities.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Sale.
 */
@Entity
@Table(name = "sale")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Sale implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @NotNull
    @Column(name = "amount_to_pay", precision = 21, scale = 2, nullable = false)
    private BigDecimal amountToPay;

    @NotNull
    @Column(name = "amount_paid", precision = 21, scale = 2, nullable = false)
    private BigDecimal amountPaid;

    @Column(name = "remaining_amount", precision = 21, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_company")
    private String customerCompany;

    @Column(name = "customer_contact")
    private String customerContact;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "company", "permissions" }, allowSetters = true)
    private AppUser seller;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sale id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return this.date;
    }

    public Sale date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public BigDecimal getAmountToPay() {
        return this.amountToPay;
    }

    public Sale amountToPay(BigDecimal amountToPay) {
        this.setAmountToPay(amountToPay);
        return this;
    }

    public void setAmountToPay(BigDecimal amountToPay) {
        this.amountToPay = amountToPay;
    }

    public BigDecimal getAmountPaid() {
        return this.amountPaid;
    }

    public Sale amountPaid(BigDecimal amountPaid) {
        this.setAmountPaid(amountPaid);
        return this;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getRemainingAmount() {
        return this.remainingAmount;
    }

    public Sale remainingAmount(BigDecimal remainingAmount) {
        this.setRemainingAmount(remainingAmount);
        return this;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public Sale customerName(String customerName) {
        this.setCustomerName(customerName);
        return this;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCompany() {
        return this.customerCompany;
    }

    public Sale customerCompany(String customerCompany) {
        this.setCustomerCompany(customerCompany);
        return this;
    }

    public void setCustomerCompany(String customerCompany) {
        this.customerCompany = customerCompany;
    }

    public String getCustomerContact() {
        return this.customerContact;
    }

    public Sale customerContact(String customerContact) {
        this.setCustomerContact(customerContact);
        return this;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Sale company(Company company) {
        this.setCompany(company);
        return this;
    }

    public AppUser getSeller() {
        return this.seller;
    }

    public void setSeller(AppUser appUser) {
        this.seller = appUser;
    }

    public Sale seller(AppUser appUser) {
        this.setSeller(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sale)) {
            return false;
        }
        return getId() != null && getId().equals(((Sale) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sale{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", amountToPay=" + getAmountToPay() +
            ", amountPaid=" + getAmountPaid() +
            ", remainingAmount=" + getRemainingAmount() +
            ", customerName='" + getCustomerName() + "'" +
            ", customerCompany='" + getCustomerCompany() + "'" +
            ", customerContact='" + getCustomerContact() + "'" +
            "}";
    }
}
