package com.commercial.activities.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.commercial.activities.domain.Sale} entity. This class is used
 * in {@link com.commercial.activities.web.rest.SaleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sales?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter date;

    private BigDecimalFilter amountToPay;

    private BigDecimalFilter amountPaid;

    private BigDecimalFilter remainingAmount;

    private StringFilter customerName;

    private StringFilter customerCompany;

    private StringFilter customerContact;

    private LongFilter companyId;

    private LongFilter sellerId;

    private Boolean distinct;

    public SaleCriteria() {}

    public SaleCriteria(SaleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.date = other.optionalDate().map(InstantFilter::copy).orElse(null);
        this.amountToPay = other.optionalAmountToPay().map(BigDecimalFilter::copy).orElse(null);
        this.amountPaid = other.optionalAmountPaid().map(BigDecimalFilter::copy).orElse(null);
        this.remainingAmount = other.optionalRemainingAmount().map(BigDecimalFilter::copy).orElse(null);
        this.customerName = other.optionalCustomerName().map(StringFilter::copy).orElse(null);
        this.customerCompany = other.optionalCustomerCompany().map(StringFilter::copy).orElse(null);
        this.customerContact = other.optionalCustomerContact().map(StringFilter::copy).orElse(null);
        this.companyId = other.optionalCompanyId().map(LongFilter::copy).orElse(null);
        this.sellerId = other.optionalSellerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SaleCriteria copy() {
        return new SaleCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getDate() {
        return date;
    }

    public Optional<InstantFilter> optionalDate() {
        return Optional.ofNullable(date);
    }

    public InstantFilter date() {
        if (date == null) {
            setDate(new InstantFilter());
        }
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public BigDecimalFilter getAmountToPay() {
        return amountToPay;
    }

    public Optional<BigDecimalFilter> optionalAmountToPay() {
        return Optional.ofNullable(amountToPay);
    }

    public BigDecimalFilter amountToPay() {
        if (amountToPay == null) {
            setAmountToPay(new BigDecimalFilter());
        }
        return amountToPay;
    }

    public void setAmountToPay(BigDecimalFilter amountToPay) {
        this.amountToPay = amountToPay;
    }

    public BigDecimalFilter getAmountPaid() {
        return amountPaid;
    }

    public Optional<BigDecimalFilter> optionalAmountPaid() {
        return Optional.ofNullable(amountPaid);
    }

    public BigDecimalFilter amountPaid() {
        if (amountPaid == null) {
            setAmountPaid(new BigDecimalFilter());
        }
        return amountPaid;
    }

    public void setAmountPaid(BigDecimalFilter amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimalFilter getRemainingAmount() {
        return remainingAmount;
    }

    public Optional<BigDecimalFilter> optionalRemainingAmount() {
        return Optional.ofNullable(remainingAmount);
    }

    public BigDecimalFilter remainingAmount() {
        if (remainingAmount == null) {
            setRemainingAmount(new BigDecimalFilter());
        }
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimalFilter remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public StringFilter getCustomerName() {
        return customerName;
    }

    public Optional<StringFilter> optionalCustomerName() {
        return Optional.ofNullable(customerName);
    }

    public StringFilter customerName() {
        if (customerName == null) {
            setCustomerName(new StringFilter());
        }
        return customerName;
    }

    public void setCustomerName(StringFilter customerName) {
        this.customerName = customerName;
    }

    public StringFilter getCustomerCompany() {
        return customerCompany;
    }

    public Optional<StringFilter> optionalCustomerCompany() {
        return Optional.ofNullable(customerCompany);
    }

    public StringFilter customerCompany() {
        if (customerCompany == null) {
            setCustomerCompany(new StringFilter());
        }
        return customerCompany;
    }

    public void setCustomerCompany(StringFilter customerCompany) {
        this.customerCompany = customerCompany;
    }

    public StringFilter getCustomerContact() {
        return customerContact;
    }

    public Optional<StringFilter> optionalCustomerContact() {
        return Optional.ofNullable(customerContact);
    }

    public StringFilter customerContact() {
        if (customerContact == null) {
            setCustomerContact(new StringFilter());
        }
        return customerContact;
    }

    public void setCustomerContact(StringFilter customerContact) {
        this.customerContact = customerContact;
    }

    public LongFilter getCompanyId() {
        return companyId;
    }

    public Optional<LongFilter> optionalCompanyId() {
        return Optional.ofNullable(companyId);
    }

    public LongFilter companyId() {
        if (companyId == null) {
            setCompanyId(new LongFilter());
        }
        return companyId;
    }

    public void setCompanyId(LongFilter companyId) {
        this.companyId = companyId;
    }

    public LongFilter getSellerId() {
        return sellerId;
    }

    public Optional<LongFilter> optionalSellerId() {
        return Optional.ofNullable(sellerId);
    }

    public LongFilter sellerId() {
        if (sellerId == null) {
            setSellerId(new LongFilter());
        }
        return sellerId;
    }

    public void setSellerId(LongFilter sellerId) {
        this.sellerId = sellerId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SaleCriteria that = (SaleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(amountToPay, that.amountToPay) &&
            Objects.equals(amountPaid, that.amountPaid) &&
            Objects.equals(remainingAmount, that.remainingAmount) &&
            Objects.equals(customerName, that.customerName) &&
            Objects.equals(customerCompany, that.customerCompany) &&
            Objects.equals(customerContact, that.customerContact) &&
            Objects.equals(companyId, that.companyId) &&
            Objects.equals(sellerId, that.sellerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            date,
            amountToPay,
            amountPaid,
            remainingAmount,
            customerName,
            customerCompany,
            customerContact,
            companyId,
            sellerId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDate().map(f -> "date=" + f + ", ").orElse("") +
            optionalAmountToPay().map(f -> "amountToPay=" + f + ", ").orElse("") +
            optionalAmountPaid().map(f -> "amountPaid=" + f + ", ").orElse("") +
            optionalRemainingAmount().map(f -> "remainingAmount=" + f + ", ").orElse("") +
            optionalCustomerName().map(f -> "customerName=" + f + ", ").orElse("") +
            optionalCustomerCompany().map(f -> "customerCompany=" + f + ", ").orElse("") +
            optionalCustomerContact().map(f -> "customerContact=" + f + ", ").orElse("") +
            optionalCompanyId().map(f -> "companyId=" + f + ", ").orElse("") +
            optionalSellerId().map(f -> "sellerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
