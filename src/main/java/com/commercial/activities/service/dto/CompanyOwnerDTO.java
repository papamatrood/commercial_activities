package com.commercial.activities.service.dto;

import com.commercial.activities.config.Constants;
import com.commercial.activities.domain.enumeration.AppUserTypeEnum;
import com.commercial.activities.domain.enumeration.GenderEnum;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO representing the owner of a {@link com.commercial.activities.domain.Company}: the
 * combination of the person's {@link com.commercial.activities.domain.User} account fields and
 * their {@link com.commercial.activities.domain.AppUser} business profile fields.
 */
public class CompanyOwnerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** AppUser id, null when creating a new owner. */
    private Long id;

    /** User id, null when creating a new owner. */
    private Long userId;

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(min = 2, max = 10)
    private String langKey;

    @NotBlank
    private String phoneNumber;

    @NotNull
    private AppUserTypeEnum type;

    private Instant birthDate;

    private String birthPlace;

    private GenderEnum gender;

    private Boolean disabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanyOwnerDTO)) {
            return false;
        }

        CompanyOwnerDTO that = (CompanyOwnerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompanyOwnerDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", login='" + getLogin() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", langKey='" + getLangKey() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", type='" + getType() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", birthPlace='" + getBirthPlace() + "'" +
            ", gender='" + getGender() + "'" +
            ", disabled='" + getDisabled() + "'" +
            "}";
    }
}
