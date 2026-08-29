package com.leaseapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "material_name", nullable = false, unique = true)
    private String materialName;

    @NotNull
    @Min(0)
    @Column(name = "no_of_stocks_available", nullable = false)
    private Integer noOfStocksAvailable;

    @NotNull
    @Min(0)
    @Column(name = "total_stocks", nullable = false)
    private Integer totalStocks;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "cost_per_day", nullable = false, precision = 12, scale = 2)
    private BigDecimal costPerDay;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Material() {
    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getNoOfStocksAvailable() {
        return noOfStocksAvailable;
    }

    public void setNoOfStocksAvailable(Integer noOfStocksAvailable) {
        this.noOfStocksAvailable = noOfStocksAvailable;
    }

    public Integer getTotalStocks() {
        return totalStocks;
    }

    public void setTotalStocks(Integer totalStocks) {
        this.totalStocks = totalStocks;
    }

    public BigDecimal getCostPerDay() {
        return costPerDay;
    }

    public void setCostPerDay(BigDecimal costPerDay) {
        this.costPerDay = costPerDay;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
