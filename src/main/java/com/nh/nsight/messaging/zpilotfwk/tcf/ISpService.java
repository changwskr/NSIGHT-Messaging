package com.nh.nsight.messaging.zpilotfwk.tcf;

/**
 * TCF BTF 업무 서비스 계약.
 * {@link com.nh.nsight.messaging.zpilotfwk.common.as.SP_COMMON},
 * {@link com.nh.nsight.messaging.zpilotfwk.order.as.SP_ORDER},
 * {@link com.nh.nsight.messaging.zpilotfwk.comrc.as.SP_COMRC} 등이 구현한다.
 */
public interface ISpService {

    /** 라우팅 식별자 — {@code eventNo} 접두사와 동일 (예: SP_ORDER) */
    String serviceId();

    EPlatonEvent execute(EPlatonEvent event);
}
