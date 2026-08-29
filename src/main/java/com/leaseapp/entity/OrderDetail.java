package com.leaseapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_number", nullable = false, insertable = false, updatable = false)
    private Order order;

    @NotNull
    @Column(name = "order_number", nullable = false)
    private UUID orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false, insertable = false, updatable = false)
    private Material material;

    @NotNull
    @Column(name = "material_id", nullable = false)
    private UUID materialId;

    // Denormalized snapshot of the material name at lend time, so history
    // stays readable even if the material is later renamed or deleted.
    @NotNull
    @Column(name = "material_name", nullable = false)
    private String materialName;

    @NotNull
    @Min(1)
    @Column(name = "no_of_material_required", nullable = false)
    private Integer noOfMaterialRequired;

    @Column(name = "lent_timestamp", nullable = false)
    private OffsetDateTime lentTimestamp;

    @Column(name = "return_timestamp")
    private OffsetDateTime returnTimestamp;

    @Column(name = "cost")
    private BigDecimal cost = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public OrderDetail() {
    }

    @PrePersist
    void prePersist() {
        if (lentTimestamp == null) {
            lentTimestamp = OffsetDateTime.now();
        }
    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public UUID getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(UUID orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Material getMaterial() {
        return material;
    }

    public UUID getMaterialId() {
        return materialId;
    }

    public void setMaterialId(UUID materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getNoOfMaterialRequired() {
        return noOfMaterialRequired;
    }

    public void setNoOfMaterialRequired(Integer noOfMaterialRequired) {
        this.noOfMaterialRequired = noOfMaterialRequired;
    }

    public OffsetDateTime getLentTimestamp() {
        return lentTimestamp;
    }

    public void setLentTimestamp(OffsetDateTime lentTimestamp) {
        this.lentTimestamp = lentTimestamp;
    }

    public OffsetDateTime getReturnTimestamp() {
        return returnTimestamp;
    }

    public void setReturnTimestamp(OffsetDateTime returnTimestamp) {
        this.returnTimestamp = returnTimestamp;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
