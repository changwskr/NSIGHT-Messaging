package com.nh.nsight.messaging.common.error;

import com.nh.nsight.messaging.common.response.StandardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardResponse<Void>> handleBusiness(BusinessException ex) {
        log.warn("[BIZ-ERROR] code={} message={}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponse.fail("MSG-ERROR-001", "globalException", ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));
        log.warn("[VALIDATION-ERROR] {}", detail);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponse.fail("MSG-ERROR-001", "globalException", ErrorCode.VAL_INVALID_REQUEST, detail));
    }

    @ExceptionHandler(QueryTimeoutException.class)
    public ResponseEntity<StandardResponse<Void>> handleQueryTimeout(QueryTimeoutException ex) {
        log.error("[DB-QUERY-TIMEOUT] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(StandardResponse.fail("MSG-ERROR-001", "globalException", ErrorCode.DB_QUERY_TIMEOUT, "Query timeout"));
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<StandardResponse<Void>> handlePoolTimeout(DataAccessResourceFailureException ex) {
        log.error("[DB-RESOURCE-ERROR] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(StandardResponse.fail("MSG-ERROR-001", "globalException", ErrorCode.DB_POOL_TIMEOUT, "DB resource unavailable"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[VALIDATION-ERROR] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponse.fail("MSG-ERROR-001", "globalException", ErrorCode.VAL_INVALID_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResource(NoResourceFoundException ex) {
        String path = ex.getResourcePath();
        if (isFaviconPath(path)) {
            return ResponseEntity.notFound().build();
        }
        if (path != null && path.startsWith("api/")) {
            log.warn("[NOT-FOUND] path={}", path);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(StandardResponse.fail("MSG-ERROR-001", "globalException", ErrorCode.API_NOT_FOUND,
                            "요청 경로를 찾을 수 없습니다: /" + path));
        }
        log.warn("[NOT-FOUND] path={}", path);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Void>> handleUnknown(Exception ex) {
        log.error("[SYS-UNKNOWN]", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StandardResponse.fail("MSG-ERROR-001", "globalException", ErrorCode.SYS_UNKNOWN, "관리자에게 문의하십시오."));
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + "=" + error.getDefaultMessage();
    }

    private boolean isFaviconPath(String path) {
        return path != null && path.endsWith("favicon.ico");
    }
}
