package com.nh.nsight.messaging.xpilotframewrok.common.processor;

import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;

/** 전처리 프로세서 SPI. 구현체는 @Order로 실행 순서가 결정된다. */
@FunctionalInterface
public interface FwPreProcessor {
    void process(FwProcessContext context);
}