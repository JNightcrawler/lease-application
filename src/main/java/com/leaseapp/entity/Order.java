package com.leaseapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_number", updatable = false, nullable = false)
    private UUID orderNumber;

    @NotBlank
    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "create_timestamp", nullable = false)
    private OffsetDateTime createTimestamp;

    @Column(name = "closing_timestamp")
    private OffsetDateTime closingTimestamp;

    @Column(name = "is_closed", nullable = false)
    private boolean isClosed = false;

    @Column(name = "approximate_date_to_return")
    private OffsetDateTime approximateDateToReturn;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Order() {
    }

    @PrePersist
    void prePersist() {
        if (createTimestamp == null) {
            createTimestamp = OffsetDateTime.now();
        }
    }

    // Getters and setters

    public UUID getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(UUID orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public OffsetDateTime getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(OffsetDateTime createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public OffsetDateTime getClosingTimestamp() {
        return closingTimestamp;
    }

    public void setClosingTimestamp(OffsetDateTime closingTimestamp) {
        this.closingTimestamp = closingTimestamp;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public OffsetDateTime getApproximateDateToReturn() {
        return approximateDateToReturn;
    }

    public void setApproximateDateToReturn(OffsetDateTime approximateDateToReturn) {
        this.approximateDateToReturn = approximateDateToReturn;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
