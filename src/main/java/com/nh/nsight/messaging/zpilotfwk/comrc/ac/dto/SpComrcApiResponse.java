package com.nh.nsight.messaging.zpilotfwk.comrc.ac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpComrcApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> SpComrcApiResponse<T> ok(T data) {
        SpComrcApiResponse<T> response = new SpComrcApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> SpComrcApiResponse<T> fail(String message) {
        SpComrcApiResponse<T> response = new SpComrcApiResponse<>();
        response.success = false;
        response.message = message;
        return response;
    }

    public static <T> SpComrcApiResponse<T> fail(String message, T data) {
        SpComrcApiResponse<T> response = fail(message);
        response.data = data;
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
