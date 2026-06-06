package com.nh.nsight.messaging.xpilotframewrok.common.exception;

/** xpilotframewrok 모듈 전용 비즈니스/검증 예외. errorCode와 메시지를 함께 전달한다. */
public class FwFrameworkException extends RuntimeException {

    private final String errorCode;

    public FwFrameworkException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}