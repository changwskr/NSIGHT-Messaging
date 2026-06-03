package com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto;

import java.util.List;

/**
 * AC REST 공통 응답 래퍼.
 */
public class UserApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Integer count;
    private Integer pageNo;
    private Integer pageSize;

    public static <T> UserApiResponse<T> ok(T data) {
        UserApiResponse<T> response = new UserApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> UserApiResponse<T> ok(T data, String message) {
        UserApiResponse<T> response = ok(data);
        response.message = message;
        return response;
    }

    public static UserApiResponse<List<UserProfileCDTO>> okList(List<UserProfileCDTO> data) {
        UserApiResponse<List<UserProfileCDTO>> response = new UserApiResponse<>();
        response.success = true;
        response.data = data;
        response.count = data == null ? 0 : data.size();
        return response;
    }

    public static UserApiResponse<List<UserProfileCDTO>> okPage(
            List<UserProfileCDTO> data,
            long totalCount,
            int pageNo,
            int pageSize
    ) {
        UserApiResponse<List<UserProfileCDTO>> response = new UserApiResponse<>();
        response.success = true;
        response.data = data;
        response.count = (int) totalCount;
        response.pageNo = pageNo;
        response.pageSize = pageSize;
        return response;
    }

    public static <T> UserApiResponse<T> fail(String message) {
        UserApiResponse<T> response = new UserApiResponse<>();
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

    public Integer getCount() {
        return count;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}
