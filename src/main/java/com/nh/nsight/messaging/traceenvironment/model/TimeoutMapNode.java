package com.nh.nsight.messaging.traceenvironment.model;

public record TimeoutMapNode(
        int order,
        String layer,
        String label,
        long timeoutMs,
        String displayValue,
        AssessmentStatus status,
        String note,
        String sourceFile,
        String propertyKey,
        String configValue,
        String guideValue
) {
}
