package com.leaseapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class OrderDetailDto {

    public static class CreateRequest {
        @NotNull(message = "orderNumber is required")
        public UUID orderNumber;

        @NotNull(message = "materialId is required")
        public UUID materialId;

        @NotNull(message = "noOfMaterialRequired is required")
        @Min(value = 1, message = "noOfMaterialRequired must be at least 1")
        public Integer noOfMaterialRequired;

        // Optional - defaults to now()
        public OffsetDateTime lentTimestamp;
    }

    public static class UpdateRequest {
        @Min(value = 1, message = "noOfMaterialRequired must be at least 1")
        public Integer noOfMaterialRequired;
    }

    public static class ReturnRequest {
        // Optional - defaults to now()
        public OffsetDateTime returnTimestamp;
    }

    public static class Response {
        public UUID id;
        public UUID orderNumber;
        public UUID materialId;
        public String materialName;
        public Integer noOfMaterialRequired;
        public OffsetDateTime lentTimestamp;
        public OffsetDateTime returnTimestamp;
        public BigDecimal cost;
    }
}
