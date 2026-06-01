package com.nh.nsight.messaging.capacitymgr.zcommonutil;

/**
 * capacitymgr 업무 예외 (KSA NewBusinessException 대체).
 */
public class CapacityBizException extends RuntimeException {

    public CapacityBizException(String message) {
        super(message);
    }

    public CapacityBizException(String message, Throwable cause) {
        super(message, cause);
    }
}
