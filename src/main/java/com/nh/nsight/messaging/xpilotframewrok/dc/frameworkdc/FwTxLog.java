package com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc;

import java.time.LocalDateTime;

/**
 * 거래 성능 로그 엔티티 (DC 계층).
 * <p>
 * POST-006, POST-007 거래 로그 적재 시 사용되며, fw_tx_log 테이블과 매핑된다.
 * 설계서 5장(후처리) 및 DC 계층 데이터 모델을 따른다.
 */
public class FwTxLog {

    /** 거래 로그 고유 식별자 (PK, DB 자동 생성) */
    private Long logId;
    /** 글로벌 요청 추적 ID (GUID) */
    private String guid;
    /** 분산 추적 ID (Trace ID) */
    private String traceId;
    /** 서비스 ID */
    private String serviceId;
    /** 요청 사용자 ID */
    private String userId;
    /** 요청 URI 경로 */
    private String requestUri;
    /** HTTP 메서드 (GET, POST 등) */
    private String httpMethod;
    /** 처리 결과 코드 */
    private String resultCode;
    /** 오류 코드 (실패 시) */
    private String errorCode;
    /** 처리 AP(어플리케이션 프로세스) ID */
    private String apId;
    /** DB 처리 소요 시간 (밀리초) */
    private Long dbTime;
    /** 외부 연동 소요 시간 (밀리초) */
    private Long extTime;
    /** 전체 처리 소요 시간 (밀리초) */
    private Long totalTime;
    /** 로그 기록 시각 (DB 기본값 또는 INSERT 시 설정) */
    private LocalDateTime logTime;

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRequestUri() { return requestUri; }
    public void setRequestUri(String requestUri) { this.requestUri = requestUri; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    public String getResultCode() { return resultCode; }
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getApId() { return apId; }
    public void setApId(String apId) { this.apId = apId; }
    public Long getDbTime() { return dbTime; }
    public void setDbTime(Long dbTime) { this.dbTime = dbTime; }
    public Long getExtTime() { return extTime; }
    public void setExtTime(Long extTime) { this.extTime = extTime; }
    public Long getTotalTime() { return totalTime; }
    public void setTotalTime(Long totalTime) { this.totalTime = totalTime; }
    public LocalDateTime getLogTime() { return logTime; }
    public void setLogTime(LocalDateTime logTime) { this.logTime = logTime; }
}
