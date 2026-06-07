package com.nh.nsight.messaging.zpilotfwk.order.ac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpOrderApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> SpOrderApiResponse<T> ok(T data) {
        SpOrderApiResponse<T> response = new SpOrderApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> SpOrderApiResponse<T> fail(String message) {
        SpOrderApiResponse<T> response = new SpOrderApiResponse<>();
        response.success = false;
        response.message = message;
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
