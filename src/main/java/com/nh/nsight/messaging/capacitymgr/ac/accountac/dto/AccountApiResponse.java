package com.nh.nsight.messaging.capacitymgr.ac.accountac.dto;

import java.util.List;

/**
 * AC REST 공통 응답 래퍼.
 */
public class AccountApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Integer count;

    public static <T> AccountApiResponse<T> ok(T data) {
        AccountApiResponse<T> response = new AccountApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> AccountApiResponse<T> ok(T data, String message) {
        AccountApiResponse<T> response = ok(data);
        response.message = message;
        return response;
    }

    public static AccountApiResponse<List<AccountCDTO>> okList(List<AccountCDTO> data) {
        AccountApiResponse<List<AccountCDTO>> response = new AccountApiResponse<>();
        response.success = true;
        response.data = data;
        response.count = data == null ? 0 : data.size();
        return response;
    }

    public static <T> AccountApiResponse<T> fail(String message) {
        AccountApiResponse<T> response = new AccountApiResponse<>();
        response.success = false;
        response.message = message;
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
