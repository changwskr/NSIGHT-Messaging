package com.nh.nsight.messaging.xpilotframewrok.common.processor;

import com.nh.nsight.messaging.common.context.RequestContext;
import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.common.exception.FwFrameworkException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * PRE-002 Header 정규화 전처리기 (common 계층).
 * HTTP 요청 헤더(RequestContext) 값을 FwProcessContext에 채우고 필수값을 검증한다.
 */
@Component
@Order(10)
public class FwHeaderPreProcessor implements FwPreProcessor {

    private static final String PROC = "FwHeaderPreProcessor";

    @Override
    public void process(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + PROC + "] process START transactionId=" + context.getTransactionId());
        RequestContext.Context req = RequestContext.get();
        // 컨텍스트에 없으면 RequestContext 헤더로 보완
        if (!StringUtils.hasText(context.getGuid())) {
            context.setGuid(req.guid());
        }
        if (!StringUtils.hasText(context.getTraceId())) {
            context.setTraceId(req.traceId());
        }
        if (!StringUtils.hasText(context.getUserId())) {
            context.setUserId(req.userId());
        }
        if (!StringUtils.hasText(context.getBranchId())) {
            context.setBranchId(req.branchId());
        }
        if (!StringUtils.hasText(context.getCenterId())) {
            context.setCenterId(req.centerId());
        }
        if (!StringUtils.hasText(context.getClientIp())) {
            context.setClientIp(req.clientIp());
        }
        // 필수값 검증
        if (!StringUtils.hasText(context.getTransactionId())) {
            throw new FwFrameworkException("HDR-001", "transactionId는 필수입니다.");
        }
        if (!StringUtils.hasText(context.getServiceId())) {
            throw new FwFrameworkException("HDR-002", "serviceId는 필수입니다.");
        }
        // 기본값 설정
        if (!StringUtils.hasText(context.getChannelId())) {
            context.setChannelId("API");
        }
        if (!StringUtils.hasText(context.getApId())) {
            context.setApId(System.getProperty("nsight.ap-id", "MSG-LOCAL-AP01"));
        }
        System.out.println("★★★★★★★ [" + PROC + "] process END transactionId=" + context.getTransactionId());
    }
}