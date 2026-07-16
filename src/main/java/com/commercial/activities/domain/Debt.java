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
 * A Debt.
 */
@Entity
@Table(name = "debt")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Debt implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "total_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "amount_paid", precision = 21, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "remaining_amount", precision = 21, scale = 2)
    private BigDecimal remainingAmount;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "company", "seller" }, allowSetters = true)
    private Sale sale;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Debt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Debt totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmountPaid() {
        return this.amountPaid;
    }

    public Debt amountPaid(BigDecimal amountPaid) {
        this.setAmountPaid(amountPaid);
        return this;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getRemainingAmount() {
        return this.remainingAmount;
    }

    public Debt remainingAmount(BigDecimal remainingAmount) {
        this.setRemainingAmount(remainingAmount);
        return this;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Instant getDate() {
        return this.date;
    }

    public Debt date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Debt company(Company company) {
        this.setCompany(company);
        return this;
    }

    public Sale getSale() {
        return this.sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Debt sale(Sale sale) {
        this.setSale(sale);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Debt)) {
            return false;
        }
        return getId() != null && getId().equals(((Debt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Debt{" +
            "id=" + getId() +
            ", totalAmount=" + getTotalAmount() +
            ", amountPaid=" + getAmountPaid() +
            ", remainingAmount=" + getRemainingAmount() +
            ", date='" + getDate() + "'" +
            "}";
    }
}
