package com.nh.nsight.messaging.xpilottpcclient.zcommonutil;

/**
 * xpilottpcclient — xpilotmessaging HTTP 호출 실패.
 */
public class TpcClientException extends RuntimeException {

    private final int httpStatus;

    public TpcClientException(String message) {
        super(message);
        this.httpStatus = 0;
    }

    public TpcClientException(String message, int httpStatus, String responseBody) {
        super(message + (responseBody != null && !responseBody.isBlank()
                ? " · body=" + truncate(responseBody, 500)
                : ""));
        this.httpStatus = httpStatus;
    }

    public TpcClientException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = 0;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    private static String truncate(String s, int max) {
        if (s == null || s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "...";
    }
}
