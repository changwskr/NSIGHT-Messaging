package com.nh.nsight.messaging.common.response;

import com.nh.nsight.messaging.common.context.RequestContext;

import java.time.OffsetDateTime;

public record StandardHeader(
        String guid,
        String traceId,
        String spanId,
        String transactionId,
        String interfaceId,
        String serviceId,
        String requestDateTime,
        String responseDateTime,
        String sourceSystemId,
        String targetSystemId,
        String channelId,
        String terminalId,
        String userId,
        String branchId,
        String centerId,
        String apId,
        String requestType,
        String messageType,
        String version,
        String clientIp
) {
    public static StandardHeader response(String transactionId, String serviceId) {
        RequestContext.Context context = RequestContext.get();
        return new StandardHeader(
                context.guid(),
                context.traceId(),
                "SPAN-APP-001",
                transactionId,
                "IF-MSG-LOCAL-001",
                serviceId,
                context.requestDateTime().toString(),
                OffsetDateTime.now().toString(),
                "MSG-MGMT-SERVICE",
                "WEBTOPSUITE",
                "BRANCH_TERMINAL",
                context.terminalId(),
                context.userId(),
                context.branchId(),
                context.centerId(),
                System.getProperty("nsight.ap-id", "MSG-LOCAL-AP01"),
                "ONLINE",
                "RESPONSE",
                "1.0",
                context.clientIp()
        );
    }
}
