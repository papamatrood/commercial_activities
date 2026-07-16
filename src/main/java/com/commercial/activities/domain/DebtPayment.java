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
 * A DebtPayment.
 */
@Entity
@Table(name = "debt_payment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DebtPayment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "amount_paid", precision = 21, scale = 2, nullable = false)
    private BigDecimal amountPaid;

    @Column(name = "remaining_amount", precision = 21, scale = 2)
    private BigDecimal remainingAmount;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "company", "sale" }, allowSetters = true)
    private Debt debt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DebtPayment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmountPaid() {
        return this.amountPaid;
    }

    public DebtPayment amountPaid(BigDecimal amountPaid) {
        this.setAmountPaid(amountPaid);
        return this;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getRemainingAmount() {
        return this.remainingAmount;
    }

    public DebtPayment remainingAmount(BigDecimal remainingAmount) {
        this.setRemainingAmount(remainingAmount);
        return this;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Instant getDate() {
        return this.date;
    }

    public DebtPayment date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Debt getDebt() {
        return this.debt;
    }

    public void setDebt(Debt debt) {
        this.debt = debt;
    }

    public DebtPayment debt(Debt debt) {
        this.setDebt(debt);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DebtPayment)) {
            return false;
        }
        return getId() != null && getId().equals(((DebtPayment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DebtPayment{" +
            "id=" + getId() +
            ", amountPaid=" + getAmountPaid() +
            ", remainingAmount=" + getRemainingAmount() +
            ", date='" + getDate() + "'" +
            "}";
    }
}
