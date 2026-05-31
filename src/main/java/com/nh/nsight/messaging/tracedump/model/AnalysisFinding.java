package com.nh.nsight.messaging.tracedump.model;

public record AnalysisFinding(
        String id,
        String category,
        String title,
        String description,
        Severity severity,
        String guideSection,
        String evidence
) {
}
