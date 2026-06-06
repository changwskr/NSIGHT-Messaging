package com.nh.nsight.messaging.xpilotframewrok.common.pipeline;

import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.common.processor.FwPreProcessor;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PRE-001~015 ??? ??.
 * Spring {@link org.springframework.core.annotation.Order} ???? {@link FwPreProcessor}? ????.
 */
@Component
public class FwPreProcessPipeline {

    private static final String PIPE = "FwPreProcessPipeline";
    private final List<FwPreProcessor> processors;

    public FwPreProcessPipeline(List<FwPreProcessor> processors) {
        this.processors = processors;
    }

    public void run(FwProcessContext context) {
        System.out.println("??????? [" + PIPE + "] run START transactionId=" + context.getTransactionId());
        for (FwPreProcessor processor : processors) {
            processor.process(context);
        }
        System.out.println("??????? [" + PIPE + "] run END transactionId=" + context.getTransactionId());
    }
}