package com.leaseapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class OrderDetailDto {

    @Schema(description = "Request payload for adding a material to an order")
    public static class CreateRequest {
        @NotNull(message = "orderNumber is required")
        @Schema(description = "UUID of the order", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        public UUID orderNumber;

        @NotNull(message = "materialId is required")
        @Schema(description = "UUID of the material being ordered", example = "550e8400-e29b-41d4-a716-446655440001", required = true)
        public UUID materialId;

        @NotNull(message = "noOfMaterialRequired is required")
        @Min(value = 1, message = "noOfMaterialRequired must be at least 1")
        @Schema(description = "Quantity of the material required", example = "5", required = true)
        public Integer noOfMaterialRequired;

        @Schema(description = "Timestamp when material is lent (defaults to current time)", example = "2024-01-15T10:30:00+00:00")
        public OffsetDateTime lentTimestamp;
    }

    @Schema(description = "Request payload for updating an order detail (line item)")
    public static class UpdateRequest {
        @Min(value = 1, message = "noOfMaterialRequired must be at least 1")
        @Schema(description = "Updated quantity", example = "7")
        public Integer noOfMaterialRequired;
    }

    @Schema(description = "Request payload for returning a material")
    public static class ReturnRequest {
        @Schema(description = "Timestamp when material is returned (defaults to current time)", example = "2024-02-15T18:00:00+00:00")
        public OffsetDateTime returnTimestamp;
    }

    @Schema(description = "Response payload containing order detail information")
    public static class Response {
        @Schema(description = "Order detail UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        public UUID id;

        @Schema(description = "Order UUID this detail belongs to", example = "550e8400-e29b-41d4-a716-446655440000")
        public UUID orderNumber;

        @Schema(description = "Material UUID", example = "550e8400-e29b-41d4-a716-446655440001")
        public UUID materialId;

        @Schema(description = "Name of the material", example = "Wheelbarrow")
        public String materialName;

        @Schema(description = "Quantity of material ordered", example = "5")
        public Integer noOfMaterialRequired;

        @Schema(description = "Timestamp when material was lent", example = "2024-01-15T10:30:00+00:00")
        public OffsetDateTime lentTimestamp;

        @Schema(description = "Timestamp when material was returned (null if not yet returned)", example = "2024-02-15T18:00:00+00:00")
        public OffsetDateTime returnTimestamp;

        @Schema(description = "Total rental cost for this line item", example = "250.00")
        public BigDecimal cost;
    }
}
