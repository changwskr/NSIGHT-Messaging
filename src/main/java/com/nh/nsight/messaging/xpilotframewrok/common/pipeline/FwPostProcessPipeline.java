package com.nh.nsight.messaging.xpilotframewrok.common.pipeline;

import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.common.processor.FwPostProcessor;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * POST-001~014 ??? ??.
 * Spring {@link org.springframework.core.annotation.Order} ???? {@link FwPostProcessor}? ????.
 */
@Component
public class FwPostProcessPipeline {

    private static final String PIPE = "FwPostProcessPipeline";
    private final List<FwPostProcessor> processors;

    public FwPostProcessPipeline(List<FwPostProcessor> processors) {
        this.processors = processors;
    }

    public void run(FwProcessContext context) {
        System.out.println("??????? [" + PIPE + "] run START transactionId=" + context.getTransactionId());
        for (FwPostProcessor processor : processors) {
            processor.process(context);
        }
        System.out.println("??????? [" + PIPE + "] run END transactionId=" + context.getTransactionId()
                + " status=" + context.getStatus());
    }
}