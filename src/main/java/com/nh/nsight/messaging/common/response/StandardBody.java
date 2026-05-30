package com.nh.nsight.messaging.common.response;

public record StandardBody<T>(
        Object request,
        T response
) {
    public static <T> StandardBody<T> response(T response) {
        return new StandardBody<>(null, response);
    }
}
