package com.leaseapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class MaterialDto {

    public static class CreateRequest {
        @NotBlank(message = "materialName is required")
        public String materialName;

        @NotNull(message = "totalStocks is required")
        @Min(value = 0, message = "totalStocks cannot be negative")
        public Integer totalStocks;

        // Optional - defaults to totalStocks if not supplied
        @Min(value = 0, message = "noOfStocksAvailable cannot be negative")
        public Integer noOfStocksAvailable;

        @NotNull(message = "costPerDay is required")
        @DecimalMin(value = "0.0", message = "costPerDay cannot be negative")
        public BigDecimal costPerDay;
    }

    public static class UpdateRequest {
        public String materialName;

        @Min(value = 0, message = "noOfStocksAvailable cannot be negative")
        public Integer noOfStocksAvailable;

        @Min(value = 0, message = "totalStocks cannot be negative")
        public Integer totalStocks;

        @DecimalMin(value = "0.0", message = "costPerDay cannot be negative")
        public BigDecimal costPerDay;
    }

    public static class Response {
        public UUID id;
        public String materialName;
        public Integer noOfStocksAvailable;
        public Integer totalStocks;
        public BigDecimal costPerDay;
        public OffsetDateTime createdAt;
        public OffsetDateTime updatedAt;
    }
}
