package com.nh.nsight.messaging.common.response;

import com.nh.nsight.messaging.common.error.ErrorCode;

public record StandardResponse<T>(
        StandardHeader header,
        StandardBody<T> body,
        StandardControl control,
        StandardSecurity security,
        StandardError error
) {
    public static <T> StandardResponse<T> success(String transactionId, String serviceId, T response) {
        return new StandardResponse<>(
                StandardHeader.response(transactionId, serviceId),
                StandardBody.response(response),
                StandardControl.online(),
                StandardSecurity.general(),
                StandardError.success()
        );
    }

    public static <T> StandardResponse<T> successPage(String transactionId, String serviceId, T response,
                                                      Integer pageNo, Integer pageSize, Long totalCount) {
        return new StandardResponse<>(
                StandardHeader.response(transactionId, serviceId),
                StandardBody.response(response),
                StandardControl.page(pageNo, pageSize, totalCount),
                StandardSecurity.general(),
                StandardError.success()
        );
    }

    public static <T> StandardResponse<T> fail(String transactionId, String serviceId, ErrorCode errorCode, String detail) {
        return new StandardResponse<>(
                StandardHeader.response(transactionId, serviceId),
                StandardBody.response(null),
                StandardControl.online(),
                StandardSecurity.general(),
                StandardError.fail(errorCode, detail)
        );
    }
}
