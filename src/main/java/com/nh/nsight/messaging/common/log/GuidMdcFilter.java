package com.nh.nsight.messaging.common.log;

import com.nh.nsight.messaging.common.context.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class GuidMdcFilter extends OncePerRequestFilter {

    private static final DateTimeFormatter GUID_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String guid = valueOrDefault(request.getHeader("X-GUID"), generateGuid());
        String traceId = valueOrDefault(request.getHeader("X-TRACE-ID"), UUID.randomUUID().toString());
        String userId = valueOrDefault(request.getHeader("X-USER-ID"), "ANONYMOUS");
        String branchId = valueOrDefault(request.getHeader("X-BRANCH-ID"), "000000");
        String centerId = valueOrDefault(request.getHeader("X-CENTER-ID"), "LOCAL");
        String terminalId = valueOrDefault(request.getHeader("X-TERMINAL-ID"), "LOCAL-TERMINAL");
        String clientIp = extractClientIp(request);

        try {
            MDC.put("guid", guid);
            MDC.put("traceId", traceId);
            MDC.put("userId", userId);
            MDC.put("branchId", branchId);
            MDC.put("centerId", centerId);
            MDC.put("clientIp", clientIp);

            RequestContext.set(new RequestContext.Context(
                    guid, traceId, userId, branchId, centerId, terminalId, clientIp, OffsetDateTime.now()));

            response.setHeader("X-GUID", guid);
            response.setHeader("X-TRACE-ID", traceId);

            filterChain.doFilter(request, response);
        } finally {
            RequestContext.clear();
            MDC.clear();
        }
    }

    private static String generateGuid() {
        return OffsetDateTime.now().format(GUID_DATE) + "-MSG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private static String valueOrDefault(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private static String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
