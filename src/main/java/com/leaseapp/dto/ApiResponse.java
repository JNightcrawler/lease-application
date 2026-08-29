package com.leaseapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API response wrapper for all endpoints")
public class ApiResponse<T> {

    @Schema(description = "Whether the request was successful", example = "true")
    private boolean success;

    @Schema(description = "Response data payload (varies by endpoint)")
    private T data;

    @Schema(description = "Success or error message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Count of items returned (for list endpoints)", example = "5")
    private Integer count;

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.success = true;
        resp.data = data;
        return resp;
    }

    public static <T> ApiResponse<T> ok(T data, int count) {
        ApiResponse<T> resp = ok(data);
        resp.count = count;
        return resp;
    }

    public static <T> ApiResponse<T> message(String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.success = true;
        resp.message = message;
        return resp;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCount() {
        return count;
    }
}
