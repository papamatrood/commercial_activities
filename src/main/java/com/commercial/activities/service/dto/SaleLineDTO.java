package com.commercial.activities.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.commercial.activities.domain.SaleLine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleLineDTO implements Serializable {

    private Long id;

    private String barcode;

    @NotNull
    private Integer quantity;

    @NotNull
    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private SaleDTO sale;

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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public SaleDTO getSale() {
        return sale;
    }

    public void setSale(SaleDTO sale) {
        this.sale = sale;
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
        if (!(o instanceof SaleLineDTO)) {
            return false;
        }

        SaleLineDTO saleLineDTO = (SaleLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, saleLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleLineDTO{" +
            "id=" + getId() +
            ", barcode='" + getBarcode() + "'" +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", totalPrice=" + getTotalPrice() +
            ", sale=" + getSale() +
            ", product=" + getProduct() +
            "}";
    }
}
