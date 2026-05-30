package com.nh.nsight.messaging.transactionmgr.dto;

import com.nh.nsight.messaging.common.response.StandardControl;
import com.nh.nsight.messaging.common.response.StandardError;
import com.nh.nsight.messaging.common.response.StandardHeader;
import com.nh.nsight.messaging.common.response.StandardBody;
import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.common.response.StandardSecurity;
import com.nh.nsight.messaging.transactionmgr.thing.TransactionLog;

import java.time.LocalDateTime;

public record TransactionLogResponse(
        Long txLogId,
        String requestUri,
        String httpMethod,
        StandardHeader header,
        StandardControl control,
        StandardSecurity security,
        StandardError error,
        LocalDateTime createdAt
) {
    public static TransactionLogResponse from(TransactionLog log) {
        StandardHeader header = new StandardHeader(
                log.getGuid(), log.getTraceId(), log.getSpanId(), log.getTransactionId(), log.getInterfaceId(),
                log.getServiceId(), log.getRequestDateTime(), log.getResponseDateTime(), log.getSourceSystemId(),
                log.getTargetSystemId(), log.getChannelId(), log.getTerminalId(), log.getUserId(), log.getBranchId(),
                log.getCenterId(), log.getApId(), log.getRequestType(), log.getMessageType(), log.getVersion(),
                log.getClientIp()
        );
        StandardControl control = new StandardControl(
                log.getCtrlTimeout() == null ? 0 : log.getCtrlTimeout(),
                log.getCtrlRetryYn() == null ? "N" : log.getCtrlRetryYn(),
                log.getCtrlRetryCount() == null ? 0 : log.getCtrlRetryCount(),
                log.getCtrlPageNo(), log.getCtrlPageSize(), log.getCtrlTotalCount()
        );
        StandardSecurity security = new StandardSecurity(
                log.getSecMaskingLevel(), log.getSecDataGrade(), log.getSecAccessPurpose(), log.getSecAuditRequiredYn()
        );
        StandardError error = new StandardError(
                log.getErrResultCode(), log.getErrResultMessage(), log.getErrErrorCode(), log.getErrErrorMessage(),
                log.getErrErrorDetail(), log.getErrErrorSystemId(), log.getErrErrorDateTime()
        );
        return new TransactionLogResponse(
                log.getTxLogId(), log.getRequestUri(), log.getHttpMethod(), header, control, security, error,
                log.getCreatedAt()
        );
    }

    public StandardResponse<Void> toStandardEnvelope() {
        return new StandardResponse<>(header, StandardBody.response(null), control, security, error);
    }
}
