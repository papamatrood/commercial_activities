package com.commercial.activities.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.commercial.activities.domain.Sale} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant date;

    @NotNull
    private BigDecimal amountToPay;

    @NotNull
    private BigDecimal amountPaid;

    private BigDecimal remainingAmount;

    private String customerName;

    private String customerCompany;

    private String customerContact;

    private CompanyDTO company;

    private AppUserDTO seller;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public BigDecimal getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(BigDecimal amountToPay) {
        this.amountToPay = amountToPay;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCompany() {
        return customerCompany;
    }

    public void setCustomerCompany(String customerCompany) {
        this.customerCompany = customerCompany;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public AppUserDTO getSeller() {
        return seller;
    }

    public void setSeller(AppUserDTO seller) {
        this.seller = seller;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaleDTO)) {
            return false;
        }

        SaleDTO saleDTO = (SaleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, saleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", amountToPay=" + getAmountToPay() +
            ", amountPaid=" + getAmountPaid() +
            ", remainingAmount=" + getRemainingAmount() +
            ", customerName='" + getCustomerName() + "'" +
            ", customerCompany='" + getCustomerCompany() + "'" +
            ", customerContact='" + getCustomerContact() + "'" +
            ", company=" + getCompany() +
            ", seller=" + getSeller() +
            "}";
    }
}
