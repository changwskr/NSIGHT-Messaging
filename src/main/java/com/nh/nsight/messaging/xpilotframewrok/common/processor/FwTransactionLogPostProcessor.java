package com.nh.nsight.messaging.xpilotframewrok.common.processor;

import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.DCFwFramework;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** POST-006/007 거래·성능 로그 적재. TB_FW_TX_LOG INSERT. */
@Component
@Order(20)
public class FwTransactionLogPostProcessor implements FwPostProcessor {

    private static final String PROC = "FwTransactionLogPostProcessor";
    private final DCFwFramework dcFwFramework;

    public FwTransactionLogPostProcessor(DCFwFramework dcFwFramework) {
        this.dcFwFramework = dcFwFramework;
    }

    @Override
    public void process(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + PROC + "] process START transactionId=" + context.getTransactionId());
        dcFwFramework.saveTxLog(context);
        System.out.println("★★★★★★★ [" + PROC + "] process END transactionId=" + context.getTransactionId());
    }
}