package com.leaseapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class MaterialDto {

    @Schema(description = "Request payload for creating a new material")
    public static class CreateRequest {
        @NotBlank(message = "materialName is required")
        @Schema(description = "Name of the material (must be unique)", example = "Wheelbarrow", required = true)
        public String materialName;

        @NotNull(message = "totalStocks is required")
        @Min(value = 0, message = "totalStocks cannot be negative")
        @Schema(description = "Total number of units owned", example = "50", required = true)
        public Integer totalStocks;

        @Min(value = 0, message = "noOfStocksAvailable cannot be negative")
        @Schema(description = "Number of units currently available for rent (defaults to totalStocks)", example = "45")
        public Integer noOfStocksAvailable;

        @NotNull(message = "costPerDay is required")
        @DecimalMin(value = "0.0", message = "costPerDay cannot be negative")
        @Schema(description = "Daily rental cost per unit", example = "50.00", required = true)
        public BigDecimal costPerDay;
    }

    @Schema(description = "Request payload for updating a material")
    public static class UpdateRequest {
        @Schema(description = "Updated material name", example = "Wheelbarrow")
        public String materialName;

        @Min(value = 0, message = "noOfStocksAvailable cannot be negative")
        @Schema(description = "Updated number of available units", example = "40")
        public Integer noOfStocksAvailable;

        @Min(value = 0, message = "totalStocks cannot be negative")
        @Schema(description = "Updated total units owned", example = "50")
        public Integer totalStocks;

        @DecimalMin(value = "0.0", message = "costPerDay cannot be negative")
        @Schema(description = "Updated daily rental cost", example = "55.00")
        public BigDecimal costPerDay;
    }

    @Schema(description = "Response payload containing material details")
    public static class Response {
        @Schema(description = "Material UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        public UUID id;

        @Schema(description = "Name of the material", example = "Wheelbarrow")
        public String materialName;

        @Schema(description = "Current number of available units", example = "45")
        public Integer noOfStocksAvailable;

        @Schema(description = "Total units owned", example = "50")
        public Integer totalStocks;

        @Schema(description = "Rental cost per unit per day", example = "50.00")
        public BigDecimal costPerDay;

        @Schema(description = "Timestamp when material was created", example = "2024-01-15T10:30:00+00:00")
        public OffsetDateTime createdAt;
        public OffsetDateTime updatedAt;
    }
}
