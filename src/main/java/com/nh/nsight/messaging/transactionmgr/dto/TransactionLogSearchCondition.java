package com.nh.nsight.messaging.transactionmgr.dto;

public class TransactionLogSearchCondition {
    private String guid;
    private String traceId;
    private String transactionId;
    private String serviceId;
    private String resultCode;
    private String userId;
    private Integer pageNo;
    private Integer pageSize;

    public TransactionLogSearchCondition() {
    }

    public TransactionLogSearchCondition(String guid, String traceId, String transactionId, String serviceId,
                                         String resultCode, String userId, Integer pageNo, Integer pageSize) {
        this.guid = guid;
        this.traceId = traceId;
        this.transactionId = transactionId;
        this.serviceId = serviceId;
        this.resultCode = resultCode;
        this.userId = userId;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public int getOffset() {
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 3 : pageSize;
        return (safePageNo - 1) * safePageSize;
    }

    public int getSafePageNo() {
        return pageNo == null || pageNo < 1 ? 1 : pageNo;
    }

    public int getSafePageSize() {
        return pageSize == null || pageSize < 1 ? 3 : pageSize;
    }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getResultCode() { return resultCode; }
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
