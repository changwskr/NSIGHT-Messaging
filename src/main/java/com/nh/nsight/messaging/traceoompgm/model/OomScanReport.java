package com.nh.nsight.messaging.traceoompgm.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record OomScanReport(
        String scanId,
        String projectName,
        LocalDateTime scannedAt,
        String sourceRoot,
        String configPath,
        String mapperRoot,
        int filesScanned,
        Map<String, Long> findingsBySeverity,
        List<OomRiskFinding> findings,
        boolean gatePassed,
        String gateMessage,
        String summaryMarkdown
) {
}
