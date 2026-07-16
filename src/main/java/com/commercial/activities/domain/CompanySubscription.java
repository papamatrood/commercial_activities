package com.commercial.activities.domain;

import com.commercial.activities.domain.enumeration.CompanySubscriptionStatusEnum;
import com.commercial.activities.domain.enumeration.CompanySubscriptionTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CompanySubscription.
 */
@Entity
@Table(name = "company_subscription")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CompanySubscription implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CompanySubscriptionTypeEnum type;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CompanySubscriptionStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CompanySubscription id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompanySubscriptionTypeEnum getType() {
        return this.type;
    }

    public CompanySubscription type(CompanySubscriptionTypeEnum type) {
        this.setType(type);
        return this;
    }

    public void setType(CompanySubscriptionTypeEnum type) {
        this.type = type;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public CompanySubscription startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public CompanySubscription endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public CompanySubscriptionStatusEnum getStatus() {
        return this.status;
    }

    public CompanySubscription status(CompanySubscriptionStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CompanySubscriptionStatusEnum status) {
        this.status = status;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public CompanySubscription company(Company company) {
        this.setCompany(company);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanySubscription)) {
            return false;
        }
        return getId() != null && getId().equals(((CompanySubscription) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompanySubscription{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
