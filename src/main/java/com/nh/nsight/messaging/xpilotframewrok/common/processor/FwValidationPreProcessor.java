package com.nh.nsight.messaging.xpilotframewrok.common.processor;

import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.common.exception.FwFrameworkException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** PRE-008 입력값 검증 전처리기. guid 길이·허용 문자 등을 검사한다. */
@Component
@Order(20)
public class FwValidationPreProcessor implements FwPreProcessor {

    private static final String PROC = "FwValidationPreProcessor";

    @Override
    public void process(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + PROC + "] process START transactionId=" + context.getTransactionId());
        if (context.getGuid() != null && context.getGuid().length() > 80) {
            throw new FwFrameworkException("VAL-001", "guid 길이 초과");
        }
        if (context.getServiceId() != null && context.getServiceId().contains("<")) {
            throw new FwFrameworkException("VAL-002", "허용되지 않은 문자");
        }
        System.out.println("★★★★★★★ [" + PROC + "] process END transactionId=" + context.getTransactionId());
    }
}