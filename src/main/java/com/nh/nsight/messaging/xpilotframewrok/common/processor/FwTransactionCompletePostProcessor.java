package com.nh.nsight.messaging.xpilotframewrok.common.processor;

import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.DCFwFramework;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** POST-009 거래 상태 SUCCESS/FAIL 완료. 소요시간 계산 후 TB_FW_TX_STATUS UPDATE. */
@Component
@Order(10)
public class FwTransactionCompletePostProcessor implements FwPostProcessor {

    private static final String PROC = "FwTransactionCompletePostProcessor";
    private final DCFwFramework dcFwFramework;

    public FwTransactionCompletePostProcessor(DCFwFramework dcFwFramework) {
        this.dcFwFramework = dcFwFramework;
    }

    @Override
    public void process(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + PROC + "] process START transactionId=" + context.getTransactionId());
        context.setEndTime(LocalDateTime.now());
        if (context.getStartTime() != null) {
            long elapsed = java.time.Duration.between(context.getStartTime(), context.getEndTime()).toMillis();
            context.setTotalTimeMs(elapsed);
        }
        if (context.getStatus() == FwProcessContext.TxStatus.PROCESSING) {
            context.setStatus(FwProcessContext.TxStatus.SUCCESS);
            context.setResultCode("COM-0000");
        }
        dcFwFramework.updateTxStatus(context);
        System.out.println("★★★★★★★ [" + PROC + "] process END status=" + context.getStatus());
    }
}