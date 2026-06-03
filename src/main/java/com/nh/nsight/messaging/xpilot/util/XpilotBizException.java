package com.nh.nsight.messaging.xpilot.util;

/**
 * xpilot 업무 예외.
 */
public class XpilotBizException extends RuntimeException {

    public XpilotBizException(String message) {
        super(message);
    }

    public XpilotBizException(String message, Throwable cause) {
        super(message, cause);
    }
}
