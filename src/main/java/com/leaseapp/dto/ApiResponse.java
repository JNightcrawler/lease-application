package com.leaseapp.dto;

public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
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
