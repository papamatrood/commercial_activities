package com.commercial.activities.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * A combined DTO used to create or edit a {@link com.commercial.activities.domain.Company}
 * together with its owner ({@link com.commercial.activities.domain.User} + {@link com.commercial.activities.domain.AppUser})
 * in a single request.
 */
public class CompanyWithOwnerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Valid
    private CompanyDTO company;

    @NotNull
    @Valid
    private CompanyOwnerDTO owner;

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public CompanyOwnerDTO getOwner() {
        return owner;
    }

    public void setOwner(CompanyOwnerDTO owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "CompanyWithOwnerDTO{" + "company=" + company + ", owner=" + owner + "}";
    }
}
