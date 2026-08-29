package com.leaseapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDto {

    @Schema(description = "Request payload for creating a new order")
    public static class CreateRequest {
        @NotBlank(message = "mobileNumber is required")
        @Schema(description = "Customer mobile number", example = "9876543210", required = true)
        public String mobileNumber;

        @Schema(description = "Approximate date when materials will be returned", example = "2024-02-15T18:00:00+00:00")
        public OffsetDateTime approximateDateToReturn;
    }

    @Schema(description = "Request payload for updating an order")
    public static class UpdateRequest {
        @Schema(description = "Updated customer mobile number", example = "9876543210")
        public String mobileNumber;

        @Schema(description = "Updated approximate return date", example = "2024-02-20T18:00:00+00:00")
        public OffsetDateTime approximateDateToReturn;
    }

    @Schema(description = "Response payload containing order details with all line items")
    public static class Response {
        @Schema(description = "Unique order number (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        public UUID orderNumber;

        @Schema(description = "Customer mobile number", example = "9876543210")
        public String mobileNumber;

        @Schema(description = "Timestamp when order was created", example = "2024-01-15T10:30:00+00:00")
        public OffsetDateTime createTimestamp;

        @Schema(description = "Timestamp when order was closed (null if still open)", example = "2024-02-15T18:00:00+00:00")
        public OffsetDateTime closingTimestamp;

        @Schema(description = "Whether the order is closed", example = "false")
        public boolean isClosed;

        @Schema(description = "Approximate date when materials should be returned", example = "2024-02-15T18:00:00+00:00")
        public OffsetDateTime approximateDateToReturn;

        @Schema(description = "List of materials/items in this order")
        public List<OrderDetailDto.Response> orderDetails;
    }
}
