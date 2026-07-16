package com.commercial.activities.service.dto;

import com.commercial.activities.domain.enumeration.CompanySubscriptionStatusEnum;
import com.commercial.activities.domain.enumeration.CompanySubscriptionTypeEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.commercial.activities.domain.CompanySubscription} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompanySubscriptionDTO implements Serializable {

    private Long id;

    @NotNull
    private CompanySubscriptionTypeEnum type;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    private CompanySubscriptionStatusEnum status;

    private CompanyDTO company;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompanySubscriptionTypeEnum getType() {
        return type;
    }

    public void setType(CompanySubscriptionTypeEnum type) {
        this.type = type;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public CompanySubscriptionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CompanySubscriptionStatusEnum status) {
        this.status = status;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanySubscriptionDTO)) {
            return false;
        }

        CompanySubscriptionDTO companySubscriptionDTO = (CompanySubscriptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, companySubscriptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompanySubscriptionDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", company=" + getCompany() +
            "}";
    }
}
