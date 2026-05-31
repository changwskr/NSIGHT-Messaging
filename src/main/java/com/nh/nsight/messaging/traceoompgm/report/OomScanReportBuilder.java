package com.nh.nsight.messaging.traceoompgm.report;

import com.nh.nsight.messaging.traceoompgm.model.OomRiskFinding;
import com.nh.nsight.messaging.traceoompgm.model.OomRiskSeverity;
import com.nh.nsight.messaging.traceoompgm.model.OomScanReport;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OomScanReportBuilder {

    public OomScanReport build(
            String projectName,
            String sourceRoot,
            String configPath,
            String mapperRoot,
            int filesScanned,
            List<OomRiskFinding> findings,
            boolean failOnCritical
    ) {
        List<OomRiskFinding> sorted = findings.stream()
                .sorted(Comparator.comparingInt((OomRiskFinding f) -> severityOrder(f.severity()))
                        .thenComparing(OomRiskFinding::ruleId))
                .toList();

        Map<String, Long> bySeverity = new java.util.LinkedHashMap<>();
        for (OomRiskSeverity s : OomRiskSeverity.values()) {
            long count = sorted.stream().filter(f -> f.severity() == s).count();
            if (count > 0) {
                bySeverity.put(s.name(), count);
            }
        }

        long critical = bySeverity.getOrDefault(OomRiskSeverity.CRITICAL.name(), 0L);
        boolean gatePassed = !failOnCritical || critical == 0;
        String gateMessage = gatePassed
                ? "품질 Gate 통과 (CRITICAL 0건)"
                : "품질 Gate 실패 — CRITICAL " + critical + "건 해결 필요";

        return new OomScanReport(
                "OOM-SCAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                projectName,
                LocalDateTime.now(),
                sourceRoot,
                configPath,
                mapperRoot,
                filesScanned,
                bySeverity,
                sorted,
                gatePassed,
                gateMessage,
                buildMarkdown(projectName, sorted, gateMessage, filesScanned)
        );
    }

    private String buildMarkdown(String project, List<OomRiskFinding> findings, String gate, int files) {
        StringBuilder sb = new StringBuilder();
        sb.append("# NSIGHT OOM Risk Inspector 결과\n\n");
        sb.append("- 프로젝트: ").append(project).append("\n");
        sb.append("- 스캔 파일 수: ").append(files).append("\n");
        sb.append("- Gate: ").append(gate).append("\n\n");
        sb.append("## Finding 목록\n\n");
        for (OomRiskFinding f : findings) {
            sb.append("### [").append(f.severity()).append("] ").append(f.ruleId())
                    .append(" — ").append(f.title()).append("\n");
            sb.append("- 파일: `").append(f.filePath()).append("`");
            if (f.lineNumber() > 0) {
                sb.append(":").append(f.lineNumber());
            }
            sb.append("\n");
            sb.append("- 설명: ").append(f.description()).append("\n");
            sb.append("- 조치: ").append(f.recommendation()).append("\n");
            sb.append("- 근거: `").append(f.evidenceSnippet()).append("`\n\n");
        }
        return sb.toString();
    }

    private int severityOrder(OomRiskSeverity s) {
        return switch (s) {
            case CRITICAL -> 0;
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
            case INFO -> 4;
        };
    }
}
