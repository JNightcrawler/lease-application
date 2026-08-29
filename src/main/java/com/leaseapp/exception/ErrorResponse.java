package com.leaseapp.exception;

import java.time.OffsetDateTime;

public class ErrorResponse {

    private boolean success = false;
    private int status;
    private String message;
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
