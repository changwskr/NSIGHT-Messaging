package com.nh.nsight.messaging.common.log;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.transactionmgr.service.TransactionLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class StandardResponseRecordAspect {

    private static final Logger log = LoggerFactory.getLogger(StandardResponseRecordAspect.class);

    private final TransactionLogService transactionLogService;

    public StandardResponseRecordAspect(TransactionLogService transactionLogService) {
        this.transactionLogService = transactionLogService;
    }

    @AfterReturning(
            pointcut = "execution(* com.nh.nsight.messaging..controller..*(..)) "
                    + "|| execution(* com.nh.nsight.messaging.common.error.GlobalExceptionHandler..*(..))",
            returning = "result"
    )
    public void recordStandardResponse(Object result) {
        if (!(result instanceof StandardResponse<?> response)) {
            return;
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        if (MessageLogPathSupport.shouldSkipLogging(uri)) {
            return;
        }
        try {
            transactionLogService.record(response, uri, request.getMethod());
        } catch (Exception ex) {
            log.warn("[TX-LOG-SKIP] failed to persist transaction log uri={} error={}", uri, ex.getMessage());
        }
    }
}
