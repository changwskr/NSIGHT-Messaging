package com.nh.nsight.messaging.zpilotfwk.common.ac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * AC REST 공통 응답 래퍼.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpCommonApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> SpCommonApiResponse<T> ok(T data) {
        SpCommonApiResponse<T> response = new SpCommonApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> SpCommonApiResponse<T> ok(T data, String message) {
        SpCommonApiResponse<T> response = ok(data);
        response.message = message;
        return response;
    }

    public static <T> SpCommonApiResponse<T> fail(String message) {
        SpCommonApiResponse<T> response = new SpCommonApiResponse<>();
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
