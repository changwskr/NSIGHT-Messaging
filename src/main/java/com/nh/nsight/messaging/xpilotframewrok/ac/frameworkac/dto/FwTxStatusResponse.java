package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto;

/**
 * AC 계층 응답 DTO — xpilotFramework 거래(트랜잭션) 상태 조회 결과.
 */
public class FwTxStatusResponse {

    /** 거래 상태 (SUCCESS, PROCESSING, FAIL, UNKNOWN 등) */
    private String status;
    /** 재시도 허용 여부 (Y/N) */
    private String retryAllowedYn;
    /** 상태 설명 메시지 */
    private String message;
    /** 결과 코드 */
    private String resultCode;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRetryAllowedYn() { return retryAllowedYn; }
    public void setRetryAllowedYn(String retryAllowedYn) { this.retryAllowedYn = retryAllowedYn; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getResultCode() { return resultCode; }
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
}
