package com.nh.nsight.messaging.common.log;

import com.nh.nsight.messaging.config.MessageEnvelopeProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class MessageEnvelopeCaptureFilter extends OncePerRequestFilter {

    private final MessageEnvelopeFileService messageEnvelopeFileService;
    private final MessageEnvelopeProperties properties;

    public MessageEnvelopeCaptureFilter(MessageEnvelopeFileService messageEnvelopeFileService,
                                        MessageEnvelopeProperties properties) {
        this.messageEnvelopeFileService = messageEnvelopeFileService;
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        int cacheLimit = properties.getMaxBodyBytes();
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request, cacheLimit);
        }
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(request, cachingResponse);
        } finally {
            if (request instanceof ContentCachingRequestWrapper cachingRequest) {
                messageEnvelopeFileService.persistExchange(cachingRequest, cachingResponse);
            }
            cachingResponse.copyBodyToResponse();
        }
    }
}
