package com.nh.nsight.messaging.traceoompgm.model;

public record OomRiskFinding(
        String ruleId,
        OomRiskCategory category,
        OomRiskSeverity severity,
        String title,
        String description,
        String recommendation,
        String filePath,
        int lineNumber,
        String evidenceSnippet,
        int likelihoodScore
) {
}
