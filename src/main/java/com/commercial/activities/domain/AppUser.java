package com.commercial.activities.domain;

import com.commercial.activities.domain.enumeration.AppUserTypeEnum;
import com.commercial.activities.domain.enumeration.GenderEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AppUser.
 */
@Entity
@Table(name = "app_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AppUserTypeEnum type;

    @Column(name = "birth_date")
    private Instant birthDate;

    @Column(name = "birth_place")
    private String birthPlace;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderEnum gender;

    @Column(name = "disabled")
    private Boolean disabled;

    @Column(name = "disabled_date")
    private Instant disabledDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_app_user__permission",
        joinColumns = @JoinColumn(name = "app_user_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "appUsers" }, allowSetters = true)
    private Set<Permission> permissions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AppUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public AppUser phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AppUserTypeEnum getType() {
        return this.type;
    }

    public AppUser type(AppUserTypeEnum type) {
        this.setType(type);
        return this;
    }

    public void setType(AppUserTypeEnum type) {
        this.type = type;
    }

    public Instant getBirthDate() {
        return this.birthDate;
    }

    public AppUser birthDate(Instant birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(Instant birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return this.birthPlace;
    }

    public AppUser birthPlace(String birthPlace) {
        this.setBirthPlace(birthPlace);
        return this;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public GenderEnum getGender() {
        return this.gender;
    }

    public AppUser gender(GenderEnum gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public Boolean getDisabled() {
        return this.disabled;
    }

    public AppUser disabled(Boolean disabled) {
        this.setDisabled(disabled);
        return this;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Instant getDisabledDate() {
        return this.disabledDate;
    }

    public AppUser disabledDate(Instant disabledDate) {
        this.setDisabledDate(disabledDate);
        return this;
    }

    public void setDisabledDate(Instant disabledDate) {
        this.disabledDate = disabledDate;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AppUser user(User user) {
        this.setUser(user);
        return this;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public AppUser company(Company company) {
        this.setCompany(company);
        return this;
    }

    public Set<Permission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public AppUser permissions(Set<Permission> permissions) {
        this.setPermissions(permissions);
        return this;
    }

    public AppUser addPermission(Permission permission) {
        this.permissions.add(permission);
        return this;
    }

    public AppUser removePermission(Permission permission) {
        this.permissions.remove(permission);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser)) {
            return false;
        }
        return getId() != null && getId().equals(((AppUser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUser{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", type='" + getType() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", birthPlace='" + getBirthPlace() + "'" +
            ", gender='" + getGender() + "'" +
            ", disabled='" + getDisabled() + "'" +
            ", disabledDate='" + getDisabledDate() + "'" +
            "}";
    }
}
