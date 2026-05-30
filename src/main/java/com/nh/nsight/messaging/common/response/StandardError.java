package com.nh.nsight.messaging.common.response;

import com.nh.nsight.messaging.common.error.ErrorCode;

import java.time.OffsetDateTime;

public record StandardError(
        String resultCode,
        String resultMessage,
        String errorCode,
        String errorMessage,
        String errorDetail,
        String errorSystemId,
        String errorDateTime
) {
    public static StandardError success() {
        return new StandardError("SUCCESS", "정상 처리되었습니다.", "", "", "", "", "");
    }

    public static StandardError fail(ErrorCode errorCode, String detail) {
        return new StandardError(
                "FAIL",
                errorCode.getUserMessage(),
                errorCode.name(),
                errorCode.getSystemMessage(),
                detail == null ? "" : detail,
                errorCode.getErrorSystemId(),
                OffsetDateTime.now().toString()
        );
    }
}
