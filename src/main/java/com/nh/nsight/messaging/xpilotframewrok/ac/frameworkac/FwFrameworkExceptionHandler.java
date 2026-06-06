package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac;

import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilotframewrok.common.exception.FwFrameworkException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * AC(Application Controller) 계층 — xpilotFramework 전용 예외 처리기.
 * AC 패키지에서 발생하는 {@link FwFrameworkException}을 표준 실패 응답으로 변환한다.
 */
@RestControllerAdvice(basePackages = "com.nh.nsight.messaging.xpilotframewrok.ac")
public class FwFrameworkExceptionHandler {

    /**
     * 프레임워크 비즈니스 예외를 HTTP 400과 표준 실패 응답으로 처리한다.
     *
     * @param ex 발생한 프레임워크 예외
     * @return 오류 코드·메시지를 담은 표준 실패 응답
     */
    @ExceptionHandler(FwFrameworkException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public StandardResponse<Void> handle(FwFrameworkException ex) {
        // FwFrameworkException → 표준 실패 응답 변환
        return StandardResponse.fail("XPFW-ERROR-001", "xpilotFrameworkError", ErrorCode.VAL_INVALID_REQUEST, ex.getMessage());
    }
}
