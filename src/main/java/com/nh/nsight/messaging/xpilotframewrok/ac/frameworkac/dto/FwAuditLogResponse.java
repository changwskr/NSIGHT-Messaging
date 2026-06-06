package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto;

import java.time.LocalDateTime;

/**
 * AC 계층 응답 DTO — xpilotFramework 감사(Audit) 로그 목록 조회 결과.
 */
public class FwAuditLogResponse {

    /** 감사 로그 PK */
    private Long auditId;
    /** 글로벌 고유 식별자 */
    private String guid;
    /** 사용자 ID */
    private String userId;
    /** 지점 ID */
    private String branchId;
    /** 메뉴 ID */
    private String menuId;
    /** 기능 ID */
    private String functionId;
    /** 고객 ID */
    private String customerId;
    /** 행위 유형 (조회, 등록, 삭제 등) */
    private String actionType;
    /** 접근 목적 */
    private String accessPurpose;
    /** 마스킹 수준 */
    private String maskingLevel;
    /** 처리 결과 코드 */
    private String resultCode;
    /** 클라이언트 IP */
    private String clientIp;
    /** 감사 기록 시각 */
    private LocalDateTime auditTime;

    public Long getAuditId() { return auditId; }
    public void setAuditId(Long auditId) { this.auditId = auditId; }
    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }
    public String getFunctionId() { return functionId; }
    public void setFunctionId(String functionId) { this.functionId = functionId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getAccessPurpose() { return accessPurpose; }
    public void setAccessPurpose(String accessPurpose) { this.accessPurpose = accessPurpose; }
    public String getMaskingLevel() { return maskingLevel; }
    public void setMaskingLevel(String maskingLevel) { this.maskingLevel = maskingLevel; }
    public String getResultCode() { return resultCode; }
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public LocalDateTime getAuditTime() { return auditTime; }
    public void setAuditTime(LocalDateTime auditTime) { this.auditTime = auditTime; }
}
