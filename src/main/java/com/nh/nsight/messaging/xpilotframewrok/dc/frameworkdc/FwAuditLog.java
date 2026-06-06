package com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc;

import java.time.LocalDateTime;

/**
 * 감사 로그 엔티티 (DC 계층).
 * <p>
 * POST-008 감사 로그 적재 시 사용되며, fw_audit_log 테이블과 매핑된다.
 * 설계서 5장(후처리) 및 DC 계층 데이터 모델을 따른다.
 */
public class FwAuditLog {

    /** 감사 로그 고유 식별자 (PK, DB 자동 생성) */
    private Long auditId;
    /** 글로벌 요청 추적 ID (GUID) */
    private String guid;
    /** 요청 사용자 ID */
    private String userId;
    /** 처리 지점(부점) ID */
    private String branchId;
    /** 접근 메뉴 ID */
    private String menuId;
    /** 실행 기능 ID */
    private String functionId;
    /** 조회·처리 대상 고객 ID */
    private String customerId;
    /** 수행 행위 유형 (예: 조회, 등록, 수정, 삭제) */
    private String actionType;
    /** 개인정보 접근 목적 */
    private String accessPurpose;
    /** 마스킹 적용 수준 */
    private String maskingLevel;
    /** 처리 결과 코드 */
    private String resultCode;
    /** 클라이언트 IP 주소 */
    private String clientIp;
    /** 감사 로그 기록 시각 (DB 기본값 또는 INSERT 시 설정) */
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
