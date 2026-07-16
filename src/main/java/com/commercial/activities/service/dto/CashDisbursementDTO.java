package com.commercial.activities.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.commercial.activities.domain.CashDisbursement} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CashDisbursementDTO implements Serializable {

    private Long id;

    @NotNull
    private String reason;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Instant date;

    private CompanyDTO company;

    private AppUserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public AppUserDTO getUser() {
        return user;
    }

    public void setUser(AppUserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CashDisbursementDTO)) {
            return false;
        }

        CashDisbursementDTO cashDisbursementDTO = (CashDisbursementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cashDisbursementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CashDisbursementDTO{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", amount=" + getAmount() +
            ", date='" + getDate() + "'" +
            ", company=" + getCompany() +
            ", user=" + getUser() +
            "}";
    }
}
