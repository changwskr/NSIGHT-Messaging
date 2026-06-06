package com.nh.nsight.messaging.xpilotframewrok.common.context;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 프레임워크 전처리·후처리 공유 컨텍스트 (common 계층).
 * <p>
 * PRE-001~015(전처리)와 POST-001~014(후처리) 단계에서 읽고 쓰는 모든 상태를 한 객체에 담는다.
 * 설계서 10.1(컨텍스트 정의) 및 4장(전처리)·5장(후처리) 명세를 따른다.
 */
public class FwProcessContext {

    /**
     * 거래 상태 열거형.
     * PRE-012에서 PROCESSING으로 전환, POST-009에서 최종 상태로 확정된다.
     */
    public enum TxStatus {
        /** 요청 수신 완료 (초기 상태) */
        RECEIVED,
        /** 업무 처리 진행 중 (PRE-012 등록 후) */
        PROCESSING,
        /** 업무 처리 성공 */
        SUCCESS,
        /** 업무 처리 실패 */
        FAIL,
        /** 처리 시간 초과 */
        TIMEOUT,
        /** 상태 미확정 */
        UNKNOWN
    }

    // ── 공통 헤더 필드 (PRE-002 Header 전처리) ──
    /** 글로벌 고유 식별 ID (GUID) */
    private String guid;
    /** 분산 추적 ID (Trace ID) */
    private String traceId;
    /** 거래 ID (Transaction ID) */
    private String transactionId;
    /** 서비스 ID */
    private String serviceId;
    /** 인터페이스 ID */
    private String interfaceId;
    /** 채널 ID (예: API, WEB) */
    private String channelId;

    // ── 사용자·조직 식별 필드 ──
    /** 로그인 사용자 ID */
    private String userId;
    /** 소속 지점(부점) ID */
    private String branchId;
    /** 소속 센터 ID */
    private String centerId;
    /** 클라이언트 IP 주소 */
    private String clientIp;

    // ── HTTP 요청 메타 필드 ──
    /** 멱등 키 (PRE-010 중복 요청 방지) */
    private String idempotencyKey;
    /** 요청 URI 경로 */
    private String requestUri;
    /** HTTP 메서드 (GET, POST 등) */
    private String httpMethod;
    /** 처리 AP(어플리케이션 서버) ID */
    private String apId;

    // ── 거래 처리 결과 필드 ──
    /** 거래 상태 (기본값: RECEIVED) */
    private TxStatus status = TxStatus.RECEIVED;
    /** 결과 코드 (기본값: COM-0000) */
    private String resultCode = "COM-0000";
    /** 오류 코드 (실패 시) */
    private String errorCode;
    /** 오류 메시지 (실패 시) */
    private String errorMessage;

    // ── 성능 측정 필드 (POST-006, POST-007) ──
    /** DB 처리 소요 시간 (밀리초) */
    private Long dbTimeMs;
    /** 외부 연동 소요 시간 (밀리초) */
    private Long extTimeMs;
    /** 전체 처리 소요 시간 (밀리초) */
    private Long totalTimeMs;

    // ── 감사 로그 필드 (POST-008) ──
    /** 접근 메뉴 ID */
    private String menuId;
    /** 접근 기능 ID */
    private String functionId;
    /** 조회·처리 대상 고객 ID */
    private String customerId;
    /** 행위 유형 (예: 조회, 변경) */
    private String actionType;
    /** 개인정보 접근 목적 */
    private String accessPurpose;
    /** 마스킹 적용 수준 */
    private String maskingLevel;

    // ── 멱등성 제어 필드 (PRE-010) ──
    /** 재시도 허용 여부 */
    private boolean retryAllowed;
    /** 상태 안내 메시지 (중복 요청 시 등) */
    private String statusMessage;

    // ── 시각 정보 필드 ──
    /** 요청 수신 시각 (기본값: 현재 시각) */
    private LocalDateTime requestTime = LocalDateTime.now();
    /** 업무 처리 시작 시각 (PRE-012) */
    private LocalDateTime startTime;
    /** 업무 처리 종료 시각 (POST-009) */
    private LocalDateTime endTime;

    // ── 확장 속성·업무 결과 ──
    /** 프로세서 간 공유 속성 맵 (예: txStatusId) */
    private final Map<String, Object> attributes = new LinkedHashMap<>();
    /** 업무 로직 실행 결과 객체 */
    private Object businessResult;

    /** GUID 반환 */
    public String getGuid() { return guid; }
    /** GUID 설정 */
    public void setGuid(String guid) { this.guid = guid; }
    /** Trace ID 반환 */
    public String getTraceId() { return traceId; }
    /** Trace ID 설정 */
    public void setTraceId(String traceId) { this.traceId = traceId; }
    /** 거래 ID 반환 */
    public String getTransactionId() { return transactionId; }
    /** 거래 ID 설정 */
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    /** 서비스 ID 반환 */
    public String getServiceId() { return serviceId; }
    /** 서비스 ID 설정 */
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    /** 인터페이스 ID 반환 */
    public String getInterfaceId() { return interfaceId; }
    /** 인터페이스 ID 설정 */
    public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
    /** 채널 ID 반환 */
    public String getChannelId() { return channelId; }
    /** 채널 ID 설정 */
    public void setChannelId(String channelId) { this.channelId = channelId; }
    /** 사용자 ID 반환 */
    public String getUserId() { return userId; }
    /** 사용자 ID 설정 */
    public void setUserId(String userId) { this.userId = userId; }
    /** 지점 ID 반환 */
    public String getBranchId() { return branchId; }
    /** 지점 ID 설정 */
    public void setBranchId(String branchId) { this.branchId = branchId; }
    /** 센터 ID 반환 */
    public String getCenterId() { return centerId; }
    /** 센터 ID 설정 */
    public void setCenterId(String centerId) { this.centerId = centerId; }
    /** 클라이언트 IP 반환 */
    public String getClientIp() { return clientIp; }
    /** 클라이언트 IP 설정 */
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    /** 멱등 키 반환 */
    public String getIdempotencyKey() { return idempotencyKey; }
    /** 멱등 키 설정 */
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    /** 요청 URI 반환 */
    public String getRequestUri() { return requestUri; }
    /** 요청 URI 설정 */
    public void setRequestUri(String requestUri) { this.requestUri = requestUri; }
    /** HTTP 메서드 반환 */
    public String getHttpMethod() { return httpMethod; }
    /** HTTP 메서드 설정 */
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    /** AP ID 반환 */
    public String getApId() { return apId; }
    /** AP ID 설정 */
    public void setApId(String apId) { this.apId = apId; }
    /** 거래 상태 반환 */
    public TxStatus getStatus() { return status; }
    /** 거래 상태 설정 */
    public void setStatus(TxStatus status) { this.status = status; }
    /** 결과 코드 반환 */
    public String getResultCode() { return resultCode; }
    /** 결과 코드 설정 */
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    /** 오류 코드 반환 */
    public String getErrorCode() { return errorCode; }
    /** 오류 코드 설정 */
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    /** 오류 메시지 반환 */
    public String getErrorMessage() { return errorMessage; }
    /** 오류 메시지 설정 */
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    /** DB 소요 시간(ms) 반환 */
    public Long getDbTimeMs() { return dbTimeMs; }
    /** DB 소요 시간(ms) 설정 */
    public void setDbTimeMs(Long dbTimeMs) { this.dbTimeMs = dbTimeMs; }
    /** 외부 연동 소요 시간(ms) 반환 */
    public Long getExtTimeMs() { return extTimeMs; }
    /** 외부 연동 소요 시간(ms) 설정 */
    public void setExtTimeMs(Long extTimeMs) { this.extTimeMs = extTimeMs; }
    /** 전체 소요 시간(ms) 반환 */
    public Long getTotalTimeMs() { return totalTimeMs; }
    /** 전체 소요 시간(ms) 설정 */
    public void setTotalTimeMs(Long totalTimeMs) { this.totalTimeMs = totalTimeMs; }
    /** 메뉴 ID 반환 */
    public String getMenuId() { return menuId; }
    /** 메뉴 ID 설정 */
    public void setMenuId(String menuId) { this.menuId = menuId; }
    /** 기능 ID 반환 */
    public String getFunctionId() { return functionId; }
    /** 기능 ID 설정 */
    public void setFunctionId(String functionId) { this.functionId = functionId; }
    /** 고객 ID 반환 */
    public String getCustomerId() { return customerId; }
    /** 고객 ID 설정 */
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    /** 행위 유형 반환 */
    public String getActionType() { return actionType; }
    /** 행위 유형 설정 */
    public void setActionType(String actionType) { this.actionType = actionType; }
    /** 접근 목적 반환 */
    public String getAccessPurpose() { return accessPurpose; }
    /** 접근 목적 설정 */
    public void setAccessPurpose(String accessPurpose) { this.accessPurpose = accessPurpose; }
    /** 마스킹 수준 반환 */
    public String getMaskingLevel() { return maskingLevel; }
    /** 마스킹 수준 설정 */
    public void setMaskingLevel(String maskingLevel) { this.maskingLevel = maskingLevel; }
    /** 재시도 허용 여부 반환 */
    public boolean isRetryAllowed() { return retryAllowed; }
    /** 재시도 허용 여부 설정 */
    public void setRetryAllowed(boolean retryAllowed) { this.retryAllowed = retryAllowed; }
    /** 상태 안내 메시지 반환 */
    public String getStatusMessage() { return statusMessage; }
    /** 상태 안내 메시지 설정 */
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
    /** 요청 수신 시각 반환 */
    public LocalDateTime getRequestTime() { return requestTime; }
    /** 요청 수신 시각 설정 */
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    /** 처리 시작 시각 반환 */
    public LocalDateTime getStartTime() { return startTime; }
    /** 처리 시작 시각 설정 */
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    /** 처리 종료 시각 반환 */
    public LocalDateTime getEndTime() { return endTime; }
    /** 처리 종료 시각 설정 */
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    /** 확장 속성 맵 반환 */
    public Map<String, Object> getAttributes() { return attributes; }
    /** 업무 결과 반환 */
    public Object getBusinessResult() { return businessResult; }
    /** 업무 결과 설정 */
    public void setBusinessResult(Object businessResult) { this.businessResult = businessResult; }
}