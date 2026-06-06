package com.nh.nsight.messaging.xpilotframewrok.common.processor;

import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.DCFwFramework;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** PRE-012/014 거래 상태 PROCESSING 등록. TB_FW_TX_STATUS에 처리중 상태를 INSERT한다. */
@Component
@Order(40)
public class FwTransactionStartPreProcessor implements FwPreProcessor {

    private static final String PROC = "FwTransactionStartPreProcessor";
    private final DCFwFramework dcFwFramework;

    public FwTransactionStartPreProcessor(DCFwFramework dcFwFramework) {
        this.dcFwFramework = dcFwFramework;
    }

    @Override
    public void process(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + PROC + "] process START transactionId=" + context.getTransactionId());
        context.setStatus(FwProcessContext.TxStatus.PROCESSING);
        context.setStartTime(LocalDateTime.now());
        dcFwFramework.saveTxStatus(context);
        System.out.println("★★★★★★★ [" + PROC + "] process END transactionId=" + context.getTransactionId());
    }
}