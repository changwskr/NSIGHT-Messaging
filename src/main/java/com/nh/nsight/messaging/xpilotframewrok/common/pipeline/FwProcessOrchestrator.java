package com.nh.nsight.messaging.xpilotframewrok.common.pipeline;

import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.common.exception.FwFrameworkException;

import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * ???-??-??? ???????.
 * PRE ?? ? businessAction ??, finally?? POST? ?? ????.
 */
@Service
public class FwProcessOrchestrator {

    private static final String ORCH = "FwProcessOrchestrator";
    private final FwPreProcessPipeline preProcessPipeline;
    private final FwPostProcessPipeline postProcessPipeline;

    public FwProcessOrchestrator(FwPreProcessPipeline preProcessPipeline,
            FwPostProcessPipeline postProcessPipeline) {
        this.preProcessPipeline = preProcessPipeline;
        this.postProcessPipeline = postProcessPipeline;
    }

    public <T> T execute(FwProcessContext context, Supplier<T> businessAction) {

        System.out.println(
                "================== 1 [" + ORCH + "] execute START transactionId=" + context.getTransactionId());

        try {
            preProcessPipeline.run(context);
            T result = businessAction.get();

            System.out.println("============== 2 [" + ORCH + "] businessResult type="
                    + (result == null ? "null" : result.getClass().getSimpleName())
                    + " value=" + result);

            context.setBusinessResult(result);
            context.setStatus(FwProcessContext.TxStatus.SUCCESS);
            context.setResultCode("COM-0000");
            System.out.println("=======3 [" + ORCH + "] execute END transactionId=" + context.getTransactionId()
                    + " status=SUCCESS");
            return result;
        } catch (FwFrameworkException ex) {
            context.setStatus(FwProcessContext.TxStatus.FAIL);
            context.setResultCode("FAIL");
            context.setErrorCode(ex.getErrorCode());
            context.setErrorMessage(ex.getMessage());
            System.out.println("==========4 [" + ORCH + "] execute END transactionId=" + context.getTransactionId()
                    + " status=FAIL errorCode=" + ex.getErrorCode());
            throw ex;
        } catch (RuntimeException ex) {
            context.setStatus(FwProcessContext.TxStatus.FAIL);
            context.setResultCode("FAIL");
            context.setErrorCode("SYS-001");
            context.setErrorMessage(ex.getMessage());
            System.out.println("==============5 [" + ORCH + "] execute END transactionId=" + context.getTransactionId()
                    + " status=FAIL errorCode=SYS-001");
            throw ex;
        } finally {
            postProcessPipeline.run(context);
        }
    }
}
