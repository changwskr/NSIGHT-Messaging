package com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc;

import java.time.LocalDateTime;

/**
 * 거래 상태 엔티티 (DC 계층).
 * <p>
 * PRE-012, PRE-014 거래 시작 시 INSERT, POST-009 거래 완료 시 UPDATE에 사용된다.
 * fw_tx_status 테이블과 매핑되며, 설계서 4장(전처리)·5장(후처리)을 따른다.
 */
public class FwTxStatus {

    /** 거래 상태 고유 식별자 (PK, DB 자동 생성) */
    private Long txStatusId;
    /** 글로벌 요청 추적 ID (GUID) */
    private String guid;
    /** 분산 추적 ID (Trace ID) */
    private String traceId;
    /** 거래 ID (Transaction ID) */
    private String transactionId;
    /** 서비스 ID */
    private String serviceId;
    /** 요청 사용자 ID */
    private String userId;
    /** 처리 지점(부점) ID */
    private String branchId;
    /** 채널 ID (예: API, WEB) */
    private String channelId;
    /** 거래 상태 코드 (RECEIVED, PROCESSING, SUCCESS, FAIL 등) */
    private String status;
    /** 처리 결과 코드 */
    private String resultCode;
    /** 오류 코드 (실패 시) */
    private String errorCode;
    /** 요청 수신 시각 */
    private LocalDateTime requestTime;
    /** 업무 처리 시작 시각 */
    private LocalDateTime startTime;
    /** 업무 처리 종료 시각 */
    private LocalDateTime endTime;
    /** 전체 처리 소요 시간 (밀리초) */
    private Long elapsedTime;
    /** 멱등성 키 (중복 요청 방지용, PRE-010) */
    private String idempotencyKey;
    /** 재시도 허용 여부 (Y/N) */
    private String retryYn;
    /** 재시도 횟수 */
    private Integer retryCount;
    /** 레코드 최초 생성 시각 */
    private LocalDateTime createdAt;
    /** 레코드 최종 수정 시각 */
    private LocalDateTime updatedAt;

    public Long getTxStatusId() { return txStatusId; }
    public void setTxStatusId(Long txStatusId) { this.txStatusId = txStatusId; }
    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResultCode() { return resultCode; }
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Long getElapsedTime() { return elapsedTime; }
    public void setElapsedTime(Long elapsedTime) { this.elapsedTime = elapsedTime; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getRetryYn() { return retryYn; }
    public void setRetryYn(String retryYn) { this.retryYn = retryYn; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
