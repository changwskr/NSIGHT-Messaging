package com.nh.nsight.messaging.tracedump.dto;

import com.nh.nsight.messaging.tracedump.model.AnalysisFinding;
import com.nh.nsight.messaging.tracedump.model.TraceDumpAnalysisReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record TraceDumpAnalysisResponse(
        LocalDateTime analyzedAt,
        String evidencePath,
        String oomCategory,
        Map<String, Object> summary,
        List<AnalysisFinding> findings,
        String markdownReport
) {
    public static TraceDumpAnalysisResponse from(TraceDumpAnalysisReport report) {
        return new TraceDumpAnalysisResponse(
                report.analyzedAt(),
                report.evidencePath(),
                report.oomCategory(),
                report.summary(),
                report.findings(),
                report.markdownReport()
        );
    }
}
