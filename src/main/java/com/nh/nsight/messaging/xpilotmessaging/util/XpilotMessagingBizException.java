package com.nh.nsight.messaging.xpilotmessaging.util;

/**
 * xpilotmessaging 업무 예외 (AC에서 BusinessException과 함께 GlobalExceptionHandler 사용).
 */
public class XpilotMessagingBizException extends RuntimeException {

    public XpilotMessagingBizException(String message) {
        super(message);
    }
}
