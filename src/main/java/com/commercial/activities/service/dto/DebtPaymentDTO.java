package com.commercial.activities.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.commercial.activities.domain.DebtPayment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DebtPaymentDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal amountPaid;

    private BigDecimal remainingAmount;

    @NotNull
    private Instant date;

    private DebtDTO debt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public DebtDTO getDebt() {
        return debt;
    }

    public void setDebt(DebtDTO debt) {
        this.debt = debt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DebtPaymentDTO)) {
            return false;
        }

        DebtPaymentDTO debtPaymentDTO = (DebtPaymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, debtPaymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DebtPaymentDTO{" +
            "id=" + getId() +
            ", amountPaid=" + getAmountPaid() +
            ", remainingAmount=" + getRemainingAmount() +
            ", date='" + getDate() + "'" +
            ", debt=" + getDebt() +
            "}";
    }
}
