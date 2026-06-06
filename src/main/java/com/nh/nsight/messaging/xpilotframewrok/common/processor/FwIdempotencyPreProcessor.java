package com.nh.nsight.messaging.xpilotframewrok.common.processor;

import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.common.exception.FwFrameworkException;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.DCFwFramework;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxStatus;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** PRE-010 중복요청 방지. idempotencyKey 기준 SUCCESS/PROCESSING 거래를 차단한다. */
@Component
@Order(30)
public class FwIdempotencyPreProcessor implements FwPreProcessor {

    private static final String PROC = "FwIdempotencyPreProcessor";
    private final DCFwFramework dcFwFramework;

    public FwIdempotencyPreProcessor(DCFwFramework dcFwFramework) {
        this.dcFwFramework = dcFwFramework;
    }

    @Override
    public void process(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + PROC + "] process START transactionId=" + context.getTransactionId());
        if (!StringUtils.hasText(context.getIdempotencyKey())) {
            System.out.println("★★★★★★★ [" + PROC + "] process END skipped=noKey");
            return;
        }
        FwTxStatus previous = dcFwFramework.getTxStatus(
                context.getGuid(), context.getTransactionId(), context.getIdempotencyKey());
        if (previous == null) {
            System.out.println("★★★★★★★ [" + PROC + "] process END skipped=notFound");
            return;
        }
        if ("SUCCESS".equals(previous.getStatus())) {
            context.setStatus(FwProcessContext.TxStatus.SUCCESS);
            context.setStatusMessage("이미 정상 처리된 거래입니다.");
            context.setRetryAllowed(false);
            throw new FwFrameworkException("DUP-001", context.getStatusMessage());
        }
        if ("PROCESSING".equals(previous.getStatus())) {
            context.setStatus(FwProcessContext.TxStatus.PROCESSING);
            context.setStatusMessage("동일 키 거래가 처리 중입니다.");
            throw new FwFrameworkException("DUP-002", context.getStatusMessage());
        }
    }
}