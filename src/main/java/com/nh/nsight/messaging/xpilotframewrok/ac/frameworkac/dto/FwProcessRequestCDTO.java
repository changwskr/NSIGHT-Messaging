package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto;

/**
 * AC 계층 요청 DTO — xpilotFramework 거래 처리(MXPFW76001) API 입력.
 */
public class FwProcessRequestCDTO {

    /** 거래 ID */
    private String transactionId;
    /** 서비스 ID */
    private String serviceId;
    /** 멱등성 키 (중복 요청 방지) */
    private String idempotencyKey;
    /** 행위 유형 (감사 로그용) */
    private String actionType;
    /** 메뉴 ID (감사 로그용) */
    private String menuId;
    /** 기능 ID (감사 로그용) */
    private String functionId;
    /** 고객 ID (감사 로그용) */
    private String customerId;
    /** 접근 목적 (감사 로그용) */
    private String accessPurpose;
    /** 요청 URI */
    private String requestUri;
    /** HTTP 메서드 */
    private String httpMethod;

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }
    public String getFunctionId() { return functionId; }
    public void setFunctionId(String functionId) { this.functionId = functionId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getAccessPurpose() { return accessPurpose; }
    public void setAccessPurpose(String accessPurpose) { this.accessPurpose = accessPurpose; }
    public String getRequestUri() { return requestUri; }
    public void setRequestUri(String requestUri) { this.requestUri = requestUri; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
}
