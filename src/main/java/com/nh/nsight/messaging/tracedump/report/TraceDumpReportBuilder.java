package com.nh.nsight.messaging.tracedump.report;

import com.nh.nsight.messaging.tracedump.model.AnalysisFinding;
import com.nh.nsight.messaging.tracedump.model.ClassHistogramSnapshot;
import com.nh.nsight.messaging.tracedump.model.GcLogSnapshot;
import com.nh.nsight.messaging.tracedump.model.Severity;
import com.nh.nsight.messaging.tracedump.model.ThreadDumpSnapshot;
import com.nh.nsight.messaging.tracedump.dto.TraceDumpReportView;
import com.nh.nsight.messaging.tracedump.model.TraceDumpAnalysisReport;
import com.nh.nsight.messaging.tracedump.dto.TraceDumpReportView.OomCorrelationRow;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TraceDumpReportBuilder {

    public TraceDumpAnalysisReport build(
            LocalDateTime analyzedAt,
            String evidencePath,
            String oomCategory,
            List<AnalysisFinding> findings,
            List<ThreadDumpSnapshot> threads,
            List<GcLogSnapshot> gcLogs,
            int evidenceFileCount,
            TraceDumpReportView reportView
    ) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("evidenceFileCount", evidenceFileCount);
        summary.put("threadDumpCount", threads.size());
        summary.put("gcLogCount", gcLogs.size());
        summary.put("findingCount", findings.size());
        summary.put("highSeverityCount", findings.stream().filter(f -> f.severity() == Severity.HIGH).count());
        summary.put("criticalSeverityCount", findings.stream().filter(f -> f.severity() == Severity.CRITICAL).count());

        String markdown = buildMarkdown(evidencePath, oomCategory, findings, threads, gcLogs, summary, reportView);

        return new TraceDumpAnalysisReport(
                analyzedAt,
                evidencePath,
                oomCategory == null ? "UNKNOWN" : oomCategory,
                summary,
                findings.stream()
                        .sorted(Comparator.comparingInt(f -> severityOrder(f.severity())))
                        .toList(),
                markdown,
                reportView
        );
    }

    private String buildMarkdown(
            String path,
            String oomCategory,
            List<AnalysisFinding> findings,
            List<ThreadDumpSnapshot> threads,
            List<GcLogSnapshot> gcLogs,
            Map<String, Object> summary,
            TraceDumpReportView reportView
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("# NSIGHT JVM 덤프 분석 리포트\n\n");
        sb.append("- 분석 시각: ").append(LocalDateTime.now()).append("\n");
        sb.append("- 증거 경로: `").append(path).append("`\n");
        sb.append("- OOM 분류: **").append(oomCategory).append("**\n");
        sb.append("- 증거 파일 수: ").append(summary.get("evidenceFileCount")).append("\n");
        sb.append("- 발견 항목: ").append(summary.get("findingCount"))
                .append(" (HIGH: ").append(summary.get("highSeverityCount")).append(")\n\n");

        sb.append("## 요약 (가이드 1.2 흐름)\n\n");
        sb.append("1. OOM 유형 확인 → 2. 증거 수집 → 3. 유형 분리 → 4. 원인 특정 → 5. 조치\n\n");

        if (!threads.isEmpty()) {
            sb.append("## Thread Dump 요약\n\n");
            for (ThreadDumpSnapshot t : threads) {
                sb.append("- `").append(t.sourceFile()).append("`: threads=")
                        .append(t.totalThreads()).append(", BLOCKED=")
                        .append(t.blockedCount()).append(", Hikari대기=")
                        .append(t.hikariWaitCount()).append("\n");
            }
            sb.append("\n");
        }

        if (!gcLogs.isEmpty()) {
            sb.append("## GC Log 요약\n\n");
            for (GcLogSnapshot g : gcLogs) {
                sb.append("- `").append(g.sourceFile()).append("`: maxPause=")
                        .append(g.maxPauseMs()).append("ms, FullGC=")
                        .append(g.fullGcCount()).append("\n");
            }
            sb.append("\n");
        }

        if (reportView != null && reportView.oomCorrelations() != null && !reportView.oomCorrelations().isEmpty()) {
            sb.append("## OOM 로그 연계 (문제 영역 → 프로그램 → 원인)\n\n");
            for (OomCorrelationRow row : reportView.oomCorrelations()) {
                sb.append("### ").append(row.problemArea()).append("\n");
                sb.append("- 로그: ").append(row.logEvidence()).append("\n");
                sb.append("- 연관 프로그램: ").append(row.relatedProgram()).append("\n");
                sb.append("- 추정 원인: ").append(row.probableCause()).append("\n");
                sb.append("- 증거: `").append(row.evidenceFile()).append("`\n\n");
            }
        }

        sb.append("## 원인 추적 후보 (Finding)\n\n");
        for (AnalysisFinding f : findings) {
            sb.append("### [").append(f.severity()).append("] ").append(f.title()).append("\n");
            sb.append("- ID: ").append(f.id()).append("\n");
            sb.append("- 분류: ").append(f.category()).append("\n");
            sb.append("- 가이드: §").append(f.guideSection()).append("\n");
            sb.append("- 설명: ").append(f.description()).append("\n");
            sb.append("- 근거: ").append(f.evidence()).append("\n\n");
        }

        sb.append("## 권장 후속 조치\n\n");
        sb.append("- Heap Dump: Eclipse MAT Leak Suspects / Dominator Tree\n");
        sb.append("- Thread Dump: 10초 간격 3회 재수집 후 동일 패턴 비교\n");
        sb.append("- GC Log: Old Gen 추세·Humongous·Full GC 원인 연계\n");
        sb.append("- Native: NMT summary.diff, Direct Buffer, Thread 수\n");

        return sb.toString();
    }

    private static int severityOrder(Severity severity) {
        return switch (severity) {
            case CRITICAL -> 0;
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
            case INFO -> 4;
        };
    }
}
