package com.nh.nsight.messaging.common.error;

public enum ErrorCode {
    BIZ_NO_DATA("조회 결과가 없습니다.", "요청한 데이터가 존재하지 않습니다.", "MSG-MGMT-SERVICE"),
    BIZ_DUPLICATE_MESSAGE_CODE("이미 등록된 메시지 코드입니다.", "Duplicate messageCode", "MSG-MGMT-SERVICE"),
    VAL_INVALID_REQUEST("입력값을 확인해 주십시오.", "Validation failed", "MSG-MGMT-SERVICE"),
    API_NOT_FOUND("요청한 경로를 찾을 수 없습니다.", "Resource not found", "MSG-MGMT-SERVICE"),
    FILE_INVALID("파일을 확인해 주십시오.", "Invalid file request", "MSG-MGMT-SERVICE"),
    FILE_TOO_LARGE("파일 크기가 허용 범위를 초과했습니다.", "File size exceeded", "MSG-MGMT-SERVICE"),
    FILE_NOT_FOUND("파일을 찾을 수 없습니다.", "File not found on storage", "MSG-MGMT-SERVICE"),
    SEC_NO_AUTH("권한이 없습니다.", "Authorization failed", "MSG-MGMT-SERVICE"),
    DB_POOL_TIMEOUT("일시적으로 처리가 지연되고 있습니다.", "DB connection pool timeout", "DB"),
    DB_QUERY_TIMEOUT("조회 시간이 초과되었습니다.", "DB query timeout", "DB"),
    SYS_UNKNOWN("시스템 오류가 발생했습니다.", "Unknown system error", "MSG-MGMT-SERVICE");

    private final String userMessage;
    private final String systemMessage;
    private final String errorSystemId;

    ErrorCode(String userMessage, String systemMessage, String errorSystemId) {
        this.userMessage = userMessage;
        this.systemMessage = systemMessage;
        this.errorSystemId = errorSystemId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public String getErrorSystemId() {
        return errorSystemId;
    }
}
