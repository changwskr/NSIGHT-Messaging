package com.nh.nsight.messaging.zpilotfwk.tcf.routing;

import com.nh.nsight.messaging.zpilotfwk.tcf.ZpilotFwkBizException;

/** BTF 라우팅 실패 */
public class SpServiceRoutingException extends ZpilotFwkBizException {

    private final String errorCode;

    public SpServiceRoutingException(String errorCode, String message) {
        super(errorCode + ": " + message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
