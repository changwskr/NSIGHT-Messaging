package com.nh.nsight.messaging.traceenvironment.model;

public record ConcurrentFlowMapNode(
        int order,
        String layer,
        String label,
        long capacityValue,
        String displayValue,
        AssessmentStatus status,
        String note,
        String sourceFile,
        String propertyKey,
        String configValue,
        String guideValue
) {
}
