package com.nh.nsight.messaging.xpilotframewrok.common.processor;

import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.DCFwFramework;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** POST-008 감사 로그 적재. actionType 있을 때 TB_FW_AUDIT_LOG INSERT. */
@Component
@Order(30)
public class FwAuditLogPostProcessor implements FwPostProcessor {

    private static final String PROC = "FwAuditLogPostProcessor";
    private final DCFwFramework dcFwFramework;

    public FwAuditLogPostProcessor(DCFwFramework dcFwFramework) {
        this.dcFwFramework = dcFwFramework;
    }

    @Override
    public void process(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + PROC + "] process START transactionId=" + context.getTransactionId());
        dcFwFramework.saveAuditLog(context);
        System.out.println("★★★★★★★ [" + PROC + "] process END transactionId=" + context.getTransactionId());
    }
}