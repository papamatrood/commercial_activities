package com.commercial.activities.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.commercial.activities.domain.Debt} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DebtDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal totalAmount;

    private BigDecimal amountPaid;

    private BigDecimal remainingAmount;

    @NotNull
    private Instant date;

    private CompanyDTO company;

    private SaleDTO sale;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public SaleDTO getSale() {
        return sale;
    }

    public void setSale(SaleDTO sale) {
        this.sale = sale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DebtDTO)) {
            return false;
        }

        DebtDTO debtDTO = (DebtDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, debtDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DebtDTO{" +
            "id=" + getId() +
            ", totalAmount=" + getTotalAmount() +
            ", amountPaid=" + getAmountPaid() +
            ", remainingAmount=" + getRemainingAmount() +
            ", date='" + getDate() + "'" +
            ", company=" + getCompany() +
            ", sale=" + getSale() +
            "}";
    }
}
