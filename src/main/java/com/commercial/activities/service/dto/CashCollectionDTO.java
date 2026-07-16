package com.commercial.activities.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.commercial.activities.domain.CashCollection} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CashCollectionDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant date;

    @NotNull
    private BigDecimal amount;

    private CompanyDTO company;

    private AppUserDTO user;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
        if (!(o instanceof CashCollectionDTO)) {
            return false;
        }

        CashCollectionDTO cashCollectionDTO = (CashCollectionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cashCollectionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CashCollectionDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", amount=" + getAmount() +
            ", company=" + getCompany() +
            ", user=" + getUser() +
            "}";
    }
}
