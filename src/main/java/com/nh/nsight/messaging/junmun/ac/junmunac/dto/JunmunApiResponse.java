package com.nh.nsight.messaging.junmun.ac.junmunac.dto;

public class JunmunApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> JunmunApiResponse<T> ok(T data) {
        JunmunApiResponse<T> response = new JunmunApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> JunmunApiResponse<T> ok(T data, String message) {
        JunmunApiResponse<T> response = ok(data);
        response.message = message;
        return response;
    }

    public static <T> JunmunApiResponse<T> fail(String message) {
        JunmunApiResponse<T> response = new JunmunApiResponse<>();
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
