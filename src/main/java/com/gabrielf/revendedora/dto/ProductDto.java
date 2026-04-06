package com.gabrielf.revendedora.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductDto {

    private UUID id;
    private String name;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private UUID brandId;
    private String brandName;

    public ProductDto() {
    }

    public ProductDto(UUID id, String name, BigDecimal costPrice, BigDecimal salePrice, UUID brandId, String brandName) {
        this.id = id;
        this.name = name;
        this.costPrice = costPrice;
        this.salePrice = salePrice;
        this.brandId = brandId;
        this.brandName = brandName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }


}
