package com.nh.nsight.messaging.tracedump.model;

import com.nh.nsight.messaging.tracedump.dto.TraceDumpReportView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record TraceDumpAnalysisReport(
        LocalDateTime analyzedAt,
        String evidencePath,
        String oomCategory,
        Map<String, Object> summary,
        List<AnalysisFinding> findings,
        String markdownReport,
        TraceDumpReportView reportView
) {
}
