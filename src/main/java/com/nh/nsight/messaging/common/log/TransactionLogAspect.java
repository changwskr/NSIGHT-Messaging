package com.nh.nsight.messaging.common.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionLogAspect {

    private static final Logger log = LoggerFactory.getLogger(TransactionLogAspect.class);

    @Around("execution(* com.nh.nsight.messaging.message.facade..*(..))")
    public Object logFacadeTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String signature = joinPoint.getSignature().toShortString();
        try {
            log.info("[TX-START] {}", signature);
            Object result = joinPoint.proceed();
            log.info("[TX-END] {} elapsedMs={}", signature, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            log.error("[TX-ERROR] {} elapsedMs={} error={}", signature, System.currentTimeMillis() - start, ex.getMessage());
            throw ex;
        }
    }
}
