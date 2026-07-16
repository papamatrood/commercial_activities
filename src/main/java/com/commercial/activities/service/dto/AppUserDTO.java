package com.commercial.activities.service.dto;

import com.commercial.activities.domain.enumeration.AppUserTypeEnum;
import com.commercial.activities.domain.enumeration.GenderEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.commercial.activities.domain.AppUser} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppUserDTO implements Serializable {

    private Long id;

    @NotNull
    private String phoneNumber;

    @NotNull
    private AppUserTypeEnum type;

    private Instant birthDate;

    private String birthPlace;

    private GenderEnum gender;

    private Boolean disabled;

    private Instant disabledDate;

    private UserDTO user;

    private CompanyDTO company;

    private Set<PermissionDTO> permissions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AppUserTypeEnum getType() {
        return type;
    }

    public void setType(AppUserTypeEnum type) {
        this.type = type;
    }

    public Instant getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Instant birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Instant getDisabledDate() {
        return disabledDate;
    }

    public void setDisabledDate(Instant disabledDate) {
        this.disabledDate = disabledDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public Set<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionDTO> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUserDTO)) {
            return false;
        }

        AppUserDTO appUserDTO = (AppUserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appUserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUserDTO{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", type='" + getType() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", birthPlace='" + getBirthPlace() + "'" +
            ", gender='" + getGender() + "'" +
            ", disabled='" + getDisabled() + "'" +
            ", disabledDate='" + getDisabledDate() + "'" +
            ", user=" + getUser() +
            ", company=" + getCompany() +
            ", permissions=" + getPermissions() +
            "}";
    }
}
