package com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.dto;

/**
 * 로그 조회 검색 조건 DTO (DC 계층).
 * <p>
 * 거래 로그·감사 로그 목록 조회 시 AC→DC 간 검색 파라미터 전달에 사용된다.
 * 페이징 기본값 및 안전 범위 보정 로직을 포함한다.
 */
public class FwLogSearchDDTO {

    /** 글로벌 요청 추적 ID (GUID) 검색 조건 */
    private String guid;
    /** 분산 추적 ID (Trace ID) 검색 조건 */
    private String traceId;
    /** 서비스 ID 검색 조건 */
    private String serviceId;
    /** 사용자 ID 검색 조건 */
    private String userId;
    /** 처리 결과 코드 검색 조건 */
    private String resultCode;
    /** 행위 유형 검색 조건 (감사 로그용) */
    private String actionType;
    /** 요청 페이지 번호 (1부터 시작, 기본값 1) */
    private int pageNo = 1;
    /** 페이지당 건수 (기본값 20) */
    private int pageSize = 20;

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getResultCode() { return resultCode; }
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public int getPageNo() { return pageNo; }
    public void setPageNo(int pageNo) { this.pageNo = pageNo; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    /**
     * 안전한 페이지 번호를 반환한다 (최소 1).
     *
     * @return 1 이상의 페이지 번호
     */
    public int getSafePageNo() {
        return Math.max(1, pageNo);
    }

    /**
     * 안전한 페이지 크기를 반환한다 (1~100 범위로 보정).
     *
     * @return 1 이상 100 이하의 페이지 크기
     */
    public int getSafePageSize() {
        return Math.min(100, Math.max(1, pageSize));
    }

    /**
     * SQL OFFSET 값을 계산한다.
     *
     * @return (페이지번호 - 1) × 페이지크기
     */
    public int getOffset() {
        return (getSafePageNo() - 1) * getSafePageSize();
    }
}
