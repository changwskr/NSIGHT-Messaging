package com.nh.nsight.messaging.xpilot.ac.pilotac.dto;

import java.util.List;
import java.util.Map;

public class PilotApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Integer count;

    public static <T> PilotApiResponse<T> ok(T data) {
        PilotApiResponse<T> response = new PilotApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> PilotApiResponse<T> ok(T data, String message) {
        PilotApiResponse<T> response = ok(data);
        response.message = message;
        return response;
    }

    public static PilotApiResponse<List<PilotCDTO>> okList(List<PilotCDTO> data) {
        PilotApiResponse<List<PilotCDTO>> response = new PilotApiResponse<>();
        response.success = true;
        response.data = data;
        response.count = data == null ? 0 : data.size();
        return response;
    }

    public static PilotApiResponse<Map<String, Object>> okMap(Map<String, Object> data) {
        return ok(data);
    }

    public static <T> PilotApiResponse<T> fail(String message) {
        PilotApiResponse<T> response = new PilotApiResponse<>();
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
