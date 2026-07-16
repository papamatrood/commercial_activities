package com.commercial.activities.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.commercial.activities.domain.StockArrival} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockArrivalDTO implements Serializable {

    private Long id;

    private String barcode;

    @NotNull
    private Integer quantity;

    private BigDecimal amount;

    @NotNull
    private Instant date;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockArrivalDTO)) {
            return false;
        }

        StockArrivalDTO stockArrivalDTO = (StockArrivalDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockArrivalDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockArrivalDTO{" +
            "id=" + getId() +
            ", barcode='" + getBarcode() + "'" +
            ", quantity=" + getQuantity() +
            ", amount=" + getAmount() +
            ", date='" + getDate() + "'" +
            ", product=" + getProduct() +
            "}";
    }
}
