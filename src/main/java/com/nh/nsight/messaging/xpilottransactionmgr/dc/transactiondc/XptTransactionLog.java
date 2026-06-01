package com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc;

import java.time.LocalDateTime;

public class XptTransactionLog {

    private Long txLogId;
    private String requestUri;
    private String httpMethod;
    private String guid;
    private String traceId;
    private String spanId;
    private String transactionId;
    private String interfaceId;
    private String serviceId;
    private String requestDateTime;
    private String responseDateTime;
    private String sourceSystemId;
    private String targetSystemId;
    private String channelId;
    private String terminalId;
    private String userId;
    private String branchId;
    private String centerId;
    private String apId;
    private String requestType;
    private String messageType;
    private String version;
    private String clientIp;
    private Integer ctrlTimeout;
    private String ctrlRetryYn;
    private Integer ctrlRetryCount;
    private Integer ctrlPageNo;
    private Integer ctrlPageSize;
    private Long ctrlTotalCount;
    private String secMaskingLevel;
    private String secDataGrade;
    private String secAccessPurpose;
    private String secAuditRequiredYn;
    private String errResultCode;
    private String errResultMessage;
    private String errErrorCode;
    private String errErrorMessage;
    private String errErrorDetail;
    private String errErrorSystemId;
    private String errErrorDateTime;
    private LocalDateTime createdAt;

    public Long getTxLogId() { return txLogId; }
    public void setTxLogId(Long txLogId) { this.txLogId = txLogId; }
    public String getRequestUri() { return requestUri; }
    public void setRequestUri(String requestUri) { this.requestUri = requestUri; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getSpanId() { return spanId; }
    public void setSpanId(String spanId) { this.spanId = spanId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getInterfaceId() { return interfaceId; }
    public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getRequestDateTime() { return requestDateTime; }
    public void setRequestDateTime(String requestDateTime) { this.requestDateTime = requestDateTime; }
    public String getResponseDateTime() { return responseDateTime; }
    public void setResponseDateTime(String responseDateTime) { this.responseDateTime = responseDateTime; }
    public String getSourceSystemId() { return sourceSystemId; }
    public void setSourceSystemId(String sourceSystemId) { this.sourceSystemId = sourceSystemId; }
    public String getTargetSystemId() { return targetSystemId; }
    public void setTargetSystemId(String targetSystemId) { this.targetSystemId = targetSystemId; }
    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    public String getTerminalId() { return terminalId; }
    public void setTerminalId(String terminalId) { this.terminalId = terminalId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    public String getCenterId() { return centerId; }
    public void setCenterId(String centerId) { this.centerId = centerId; }
    public String getApId() { return apId; }
    public void setApId(String apId) { this.apId = apId; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public Integer getCtrlTimeout() { return ctrlTimeout; }
    public void setCtrlTimeout(Integer ctrlTimeout) { this.ctrlTimeout = ctrlTimeout; }
    public String getCtrlRetryYn() { return ctrlRetryYn; }
    public void setCtrlRetryYn(String ctrlRetryYn) { this.ctrlRetryYn = ctrlRetryYn; }
    public Integer getCtrlRetryCount() { return ctrlRetryCount; }
    public void setCtrlRetryCount(Integer ctrlRetryCount) { this.ctrlRetryCount = ctrlRetryCount; }
    public Integer getCtrlPageNo() { return ctrlPageNo; }
    public void setCtrlPageNo(Integer ctrlPageNo) { this.ctrlPageNo = ctrlPageNo; }
    public Integer getCtrlPageSize() { return ctrlPageSize; }
    public void setCtrlPageSize(Integer ctrlPageSize) { this.ctrlPageSize = ctrlPageSize; }
    public Long getCtrlTotalCount() { return ctrlTotalCount; }
    public void setCtrlTotalCount(Long ctrlTotalCount) { this.ctrlTotalCount = ctrlTotalCount; }
    public String getSecMaskingLevel() { return secMaskingLevel; }
    public void setSecMaskingLevel(String secMaskingLevel) { this.secMaskingLevel = secMaskingLevel; }
    public String getSecDataGrade() { return secDataGrade; }
    public void setSecDataGrade(String secDataGrade) { this.secDataGrade = secDataGrade; }
    public String getSecAccessPurpose() { return secAccessPurpose; }
    public void setSecAccessPurpose(String secAccessPurpose) { this.secAccessPurpose = secAccessPurpose; }
    public String getSecAuditRequiredYn() { return secAuditRequiredYn; }
    public void setSecAuditRequiredYn(String secAuditRequiredYn) { this.secAuditRequiredYn = secAuditRequiredYn; }
    public String getErrResultCode() { return errResultCode; }
    public void setErrResultCode(String errResultCode) { this.errResultCode = errResultCode; }
    public String getErrResultMessage() { return errResultMessage; }
    public void setErrResultMessage(String errResultMessage) { this.errResultMessage = errResultMessage; }
    public String getErrErrorCode() { return errErrorCode; }
    public void setErrErrorCode(String errErrorCode) { this.errErrorCode = errErrorCode; }
    public String getErrErrorMessage() { return errErrorMessage; }
    public void setErrErrorMessage(String errErrorMessage) { this.errErrorMessage = errErrorMessage; }
    public String getErrErrorDetail() { return errErrorDetail; }
    public void setErrErrorDetail(String errErrorDetail) { this.errErrorDetail = errErrorDetail; }
    public String getErrErrorSystemId() { return errErrorSystemId; }
    public void setErrErrorSystemId(String errErrorSystemId) { this.errErrorSystemId = errErrorSystemId; }
    public String getErrErrorDateTime() { return errErrorDateTime; }
    public void setErrErrorDateTime(String errErrorDateTime) { this.errErrorDateTime = errErrorDateTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
