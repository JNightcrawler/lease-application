package com.leaseapp.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDto {

    public static class CreateRequest {
        @NotBlank(message = "mobileNumber is required")
        public String mobileNumber;

        public OffsetDateTime approximateDateToReturn;
    }

    public static class UpdateRequest {
        public String mobileNumber;
        public OffsetDateTime approximateDateToReturn;
    }

    public static class Response {
        public UUID orderNumber;
        public String mobileNumber;
        public OffsetDateTime createTimestamp;
        public OffsetDateTime closingTimestamp;
        public boolean isClosed;
        public OffsetDateTime approximateDateToReturn;
        public List<OrderDetailDto.Response> orderDetails;
    }
}
