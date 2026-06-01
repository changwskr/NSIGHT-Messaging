package com.nh.nsight.messaging.xpilottransactionmgr.zcommonutil;

import com.nh.nsight.messaging.transactionmgr.thing.TransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.XptTransactionLog;

public final class TransactionLogMapperUtil {

    private TransactionLogMapperUtil() {
    }

    public static TransactionLog toLegacyLog(XptTransactionLog source) {
        if (source == null) {
            return null;
        }
        TransactionLog target = new TransactionLog();
        target.setTxLogId(source.getTxLogId());
        target.setRequestUri(source.getRequestUri());
        target.setHttpMethod(source.getHttpMethod());
        target.setGuid(source.getGuid());
        target.setTraceId(source.getTraceId());
        target.setSpanId(source.getSpanId());
        target.setTransactionId(source.getTransactionId());
        target.setInterfaceId(source.getInterfaceId());
        target.setServiceId(source.getServiceId());
        target.setRequestDateTime(source.getRequestDateTime());
        target.setResponseDateTime(source.getResponseDateTime());
        target.setSourceSystemId(source.getSourceSystemId());
        target.setTargetSystemId(source.getTargetSystemId());
        target.setChannelId(source.getChannelId());
        target.setTerminalId(source.getTerminalId());
        target.setUserId(source.getUserId());
        target.setBranchId(source.getBranchId());
        target.setCenterId(source.getCenterId());
        target.setApId(source.getApId());
        target.setRequestType(source.getRequestType());
        target.setMessageType(source.getMessageType());
        target.setVersion(source.getVersion());
        target.setClientIp(source.getClientIp());
        target.setCtrlTimeout(source.getCtrlTimeout());
        target.setCtrlRetryYn(source.getCtrlRetryYn());
        target.setCtrlRetryCount(source.getCtrlRetryCount());
        target.setCtrlPageNo(source.getCtrlPageNo());
        target.setCtrlPageSize(source.getCtrlPageSize());
        target.setCtrlTotalCount(source.getCtrlTotalCount());
        target.setSecMaskingLevel(source.getSecMaskingLevel());
        target.setSecDataGrade(source.getSecDataGrade());
        target.setSecAccessPurpose(source.getSecAccessPurpose());
        target.setSecAuditRequiredYn(source.getSecAuditRequiredYn());
        target.setErrResultCode(source.getErrResultCode());
        target.setErrResultMessage(source.getErrResultMessage());
        target.setErrErrorCode(source.getErrErrorCode());
        target.setErrErrorMessage(source.getErrErrorMessage());
        target.setErrErrorDetail(source.getErrErrorDetail());
        target.setErrErrorSystemId(source.getErrErrorSystemId());
        target.setErrErrorDateTime(source.getErrErrorDateTime());
        target.setCreatedAt(source.getCreatedAt());
        return target;
    }
}
