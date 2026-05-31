package com.nh.nsight.messaging.tracedump.report;

import com.nh.nsight.messaging.tracedump.dto.TraceDumpReportView;
import com.nh.nsight.messaging.tracedump.model.AnalysisFinding;
import com.nh.nsight.messaging.tracedump.model.ClassHistogramSnapshot;
import com.nh.nsight.messaging.tracedump.model.DumpAnalysisIndicators;
import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.EvidenceType;
import com.nh.nsight.messaging.tracedump.model.GcLogSnapshot;
import com.nh.nsight.messaging.tracedump.model.HsErrSnapshot;
import com.nh.nsight.messaging.tracedump.model.NmtSnapshot;
import com.nh.nsight.messaging.tracedump.model.OomCorrelation;
import com.nh.nsight.messaging.tracedump.model.Severity;
import com.nh.nsight.messaging.tracedump.model.ThreadDumpSnapshot;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TraceDumpReportViewBuilder {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TraceDumpReportView build(
            LocalDateTime analyzedAt,
            String evidencePath,
            String oomCategory,
            List<EvidenceFile> files,
            List<AnalysisFinding> findings,
            List<ThreadDumpSnapshot> threads,
            List<GcLogSnapshot> gcLogs,
            List<HsErrSnapshot> hsErrs,
            List<NmtSnapshot> nmts,
            List<ClassHistogramSnapshot> histograms,
            List<OomCorrelation> oomCorrelations,
            DumpAnalysisIndicators indicators
    ) {
        TraceDumpReportView.KeyIndicatorsSection keyIndicators = toKeyIndicators(indicators);
        TraceDumpReportView.FourStepGuide fourStep = buildFourStepGuide(files, findings, indicators, oomCorrelations);
        List<TraceDumpReportView.PipelineStep> pipeline = buildPipeline();
        TraceDumpReportView.OverviewSection overview = new TraceDumpReportView.OverviewSection(
                analyzedAt.format(DISPLAY),
                "NSIGHT Message Mgmt Service",
                "Spring Boot 3 / Java 17 / Tomcat",
                hsErrs.stream().findFirst()
                        .map(h -> h.problemCategory() + " / " + h.signalName())
                        .orElse("(hs_err 미수집 — GC/Console 로그 참고)"),
                mapOomCategory(oomCategory),
                severityImpact(findings, oomCorrelations)
        );

        return new TraceDumpReportView(
                analyzedAt,
                evidencePath,
                oomCategory,
                fourStep,
                pipeline,
                keyIndicators,
                overview,
                buildInventory(files, analyzedAt),
                buildGcSection(gcLogs),
                buildHeapSection(histograms, files, findings),
                buildThreadSection(threads),
                buildPrimaryJudgment(threads, gcLogs, nmts, findings, oomCategory, indicators),
                toOomRows(oomCorrelations),
                buildCauseHypotheses(findings, indicators),
                buildConclusions(findings, oomCategory),
                buildActionPlan(findings, oomCategory, indicators)
        );
    }

    private TraceDumpReportView.KeyIndicatorsSection toKeyIndicators(DumpAnalysisIndicators i) {
        return new TraceDumpReportView.KeyIndicatorsSection(
                i.javaHeapOom(),
                i.fullGcCount(),
                i.oldRegionNotReduced(),
                i.heapNotReducedAfterFullGc(),
                i.deadlockFound(),
                i.hikariWaitingThreads(),
                i.cruzApimWaitingThreads(),
                i.nmtThreadCount(),
                i.sessionCacheHint(),
                i.queryResultCacheHint(),
                i.heapDumpCollected()
        );
    }

    private TraceDumpReportView.FourStepGuide buildFourStepGuide(
            List<EvidenceFile> files,
            List<AnalysisFinding> findings,
            DumpAnalysisIndicators indicators,
            List<OomCorrelation> correlations
    ) {
        String read = "① 읽기: " + files.stream()
                .map(f -> evidenceTypeLabel(f.type()) + "(" + f.fileName() + ")")
                .collect(Collectors.joining(", "));
        String evidence = "② 증거: OOM=" + indicators.oomCategory()
                + ", FullGC=" + indicators.fullGcCount()
                + ", Deadlock=" + indicators.deadlockFound()
                + ", Hikari대기=" + indicators.hikariWaitingThreads()
                + ", CruzAPIM=" + indicators.cruzApimWaitingThreads()
                + ", SESSION_CACHE=" + indicators.sessionCacheHint();
        String judgment = "③ 판정: " + findings.stream()
                .filter(f -> f.severity() == Severity.CRITICAL || f.severity() == Severity.HIGH)
                .limit(5)
                .map(f -> f.id() + " " + f.title())
                .collect(Collectors.joining(" / "));
        if (judgment.equals("③ 판정: ")) {
            judgment = "③ 판정: 자동 규칙 HIGH/CRITICAL 미검출";
        }
        String action = "④ 조치: " + correlations.stream()
                .limit(2)
                .map(OomCorrelation::probableCause)
                .collect(Collectors.joining(" / "));
        if (action.equals("④ 조치: ")) {
            action = "④ 조치: §7 조치 계획·Finding 설명 참고";
        }
        return new TraceDumpReportView.FourStepGuide(read, evidence, judgment, action);
    }

    private List<TraceDumpReportView.PipelineStep> buildPipeline() {
        return List.of(
                new TraceDumpReportView.PipelineStep(1, "증거 수집", "hprof, thread, gc, nmt, histogram, console"),
                new TraceDumpReportView.PipelineStep(2, "파일 유형 식별", "EvidenceLoader / EvidenceType 분류"),
                new TraceDumpReportView.PipelineStep(3, "지표 추출", "Parser — Thread/GC/NMT/Histogram"),
                new TraceDumpReportView.PipelineStep(4, "분석 규칙 적용", "TraceDumpRuleEngine (HEAP/THR/GC/NMT)"),
                new TraceDumpReportView.PipelineStep(5, "원인 가설 생성", "CauseHypothesis + OomCorrelation"),
                new TraceDumpReportView.PipelineStep(6, "심각도/신뢰도", "CRITICAL > HIGH > MEDIUM > INFO"),
                new TraceDumpReportView.PipelineStep(7, "조치 권고", "ActionPlan + Finding description")
        );
    }

    private List<TraceDumpReportView.CauseHypothesisRow> buildCauseHypotheses(
            List<AnalysisFinding> findings,
            DumpAnalysisIndicators indicators
    ) {
        List<TraceDumpReportView.CauseHypothesisRow> rows = new ArrayList<>();
        if (indicators.javaHeapOom() || indicators.sessionCacheHint() || indicators.heapNotReducedAfterFullGc()) {
            rows.add(hypothesis(1, "Heap Retention / 세션·캐시 객체 증가", "High", "High",
                    "java heap space, Full GC 회수 실패, SESSION_CACHE/QUERY_RESULT_CACHE",
                    ruleIds(findings, "HEAP", "GC-002"),
                    "세션 최소 필드·캐시 TTL/최대 건수, MAT Retained 분석"));
        }
        if (indicators.deadlockFound()) {
            rows.add(hypothesis(2, "Thread Deadlock", "Critical", "High",
                    "Found one Java-level deadlock",
                    ruleIds(findings, "THR-001"),
                    "Lock 획득 순서 표준화, synchronized 범위 축소"));
        }
        if (indicators.hikariWaitingThreads() >= 5) {
            rows.add(hypothesis(3, "DB Connection Pool 고갈", "High", "Medium-High",
                    "HikariPool WAITING Thread " + indicators.hikariWaitingThreads(),
                    ruleIds(findings, "THR-002"),
                    "Slow SQL, Pool 크기, Connection 반환, Transaction 4~5초 규칙"));
        }
        if (indicators.cruzApimWaitingThreads() >= 3) {
            rows.add(hypothesis(4, "외부연계(CruzAPIM) 지연", "Medium", "Medium",
                    "CruzAPIM timeout/wait " + indicators.cruzApimWaitingThreads(),
                    ruleIds(findings, "THR-003"),
                    "Connect/Read Timeout, Circuit Breaker, Bulkhead"));
        }
        if (rows.isEmpty()) {
            rows.add(hypothesis(9, "추가 분석 필요", "Info", "Low",
                    "자동 규칙 미충족", "INFO-001",
                    "MAT·10초 간격 Thread 3회·Runbook 수행"));
        }
        return rows;
    }

    private TraceDumpReportView.CauseHypothesisRow hypothesis(
            int priority, String name, String severity, String confidence,
            String evidence, String ruleIds, String action
    ) {
        return new TraceDumpReportView.CauseHypothesisRow(
                priority, name, severity, confidence, evidence, ruleIds, action
        );
    }

    private String ruleIds(List<AnalysisFinding> findings, String... prefixes) {
        return findings.stream()
                .map(AnalysisFinding::id)
                .filter(id -> {
                    for (String p : prefixes) {
                        if (id.startsWith(p)) {
                            return true;
                        }
                    }
                    return false;
                })
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private List<TraceDumpReportView.EvidenceRow> buildInventory(List<EvidenceFile> files, LocalDateTime at) {
        return files.stream()
                .map(f -> new TraceDumpReportView.EvidenceRow(
                        evidenceTypeLabel(f.type()),
                        f.fileName(),
                        at.format(DISPLAY),
                        evidenceNote(f)
                ))
                .toList();
    }

    private TraceDumpReportView.GcAnalysisSection buildGcSection(List<GcLogSnapshot> gcLogs) {
        if (gcLogs.isEmpty()) {
            return new TraceDumpReportView.GcAnalysisSection(
                    "GC Log 미수집", 0, 0, 0, 0,
                    List.of("Pause Full", "Old regions: N->N", "Heap M->M")
            );
        }
        GcLogSnapshot g = gcLogs.stream()
                .max(Comparator.comparingInt(GcLogSnapshot::fullGcCount))
                .orElse(gcLogs.get(0));
        String summary = "Full GC " + g.fullGcCount() + "회, maxPause " + g.maxPauseMs()
                + "ms, Old고정 " + g.oldRegionUnchangedCount()
                + ", Heap미회수 " + g.heapUnchangedAfterFullGcCount();
        return new TraceDumpReportView.GcAnalysisSection(
                summary,
                g.fullGcCount(),
                g.maxPauseMs(),
                g.oldRegionUnchangedCount(),
                g.heapUnchangedAfterFullGcCount(),
                List.of(
                        "Pause Full (G1 Compaction Pause)",
                        "Old regions: N->N",
                        "HeapBefore->HeapAfter 동일",
                        "clearing soft references"
                )
        );
    }

    private List<TraceDumpReportView.OomCorrelationRow> toOomRows(List<OomCorrelation> correlations) {
        return correlations.stream()
                .map(c -> new TraceDumpReportView.OomCorrelationRow(
                        c.id(), c.problemArea(), c.logEvidence(), c.relatedProgram(),
                        c.probableCause(), c.evidenceFile(), c.severity().name()
                ))
                .toList();
    }

    private TraceDumpReportView.PrimaryJudgmentSection buildPrimaryJudgment(
            List<ThreadDumpSnapshot> threads,
            List<GcLogSnapshot> gcLogs,
            List<NmtSnapshot> nmts,
            List<AnalysisFinding> findings,
            String oomCategory,
            DumpAnalysisIndicators indicators
    ) {
        boolean deadlock = indicators.deadlockFound();
        boolean hikari = indicators.hikariWaitingThreads() >= 5;
        boolean fullGc = indicators.fullGcCount() >= 5;
        String heapUsage = indicators.javaHeapOom()
                ? "Heap OOM (java heap space)" : "Heap OOM 미확인";
        String oldRegion = indicators.oldRegionNotReduced()
                ? "Old Region 증가·회수 실패" : "Old Region — GC Log 추가 확인";
        String fullGcRepeat = fullGc ? "Full GC " + indicators.fullGcCount() + "회 이상" : "Full GC 반복 임계 미만";
        boolean nativeLow = !"METASPACE_OOM".equals(oomCategory) && !"NATIVE_THREAD_OOM".equals(oomCategory);

        return new TraceDumpReportView.PrimaryJudgmentSection(
                heapUsage,
                oldRegion,
                fullGcRepeat,
                deadlock ? "Deadlock 감지" : "Deadlock 없음",
                hikari ? "Hikari Pool 대기 >= 5" : "Pool 대기 양호",
                nativeLow ? "주원인은 Java Heap (NMT Metaspace 정상 범위 가능)" : "Native/Metaspace 점검 필요"
        );
    }

    private TraceDumpReportView.HeapAnalysisSection buildHeapSection(
            List<ClassHistogramSnapshot> histograms,
            List<EvidenceFile> files,
            List<AnalysisFinding> findings
    ) {
        List<String> heapFiles = files.stream()
                .filter(f -> f.type() == EvidenceType.HEAP_DUMP)
                .map(EvidenceFile::fileName)
                .toList();
        String top = "-";
        String suspect = "-";
        if (!histograms.isEmpty() && !histograms.get(0).topEntries().isEmpty()) {
            var entry = histograms.get(0).topEntries().get(0);
            top = entry.className() + " (instances=" + entry.instances() + ", bytes=" + entry.bytes() + ")";
            suspect = histograms.get(0).topEntries().stream().limit(5)
                    .map(ClassHistogramSnapshot.HistogramEntry::className)
                    .collect(Collectors.joining(", "));
        }
        String leak = findings.stream()
                .filter(f -> f.id().startsWith("HEAP"))
                .map(AnalysisFinding::title)
                .findFirst()
                .orElse("MAT Leak Suspects·Dominator Tree 필요");

        return new TraceDumpReportView.HeapAnalysisSection(
                top, suspect,
                heapFiles.isEmpty() ? "MAT 미실행 — hprof 수동 분석" : "MAT: Dominator Tree, GC Roots",
                leak, heapFiles
        );
    }

    private TraceDumpReportView.ThreadAnalysisSection buildThreadSection(List<ThreadDumpSnapshot> threads) {
        if (threads.isEmpty()) {
            return new TraceDumpReportView.ThreadAnalysisSection(
                    0, 0, 0, "미수집", "미수집", "미수집", List.of()
            );
        }
        ThreadDumpSnapshot primary = threads.stream()
                .max(Comparator.comparingInt(ThreadDumpSnapshot::totalThreads))
                .orElse(threads.get(0));
        List<TraceDumpReportView.ThreadDumpRow> rows = threads.stream()
                .map(t -> new TraceDumpReportView.ThreadDumpRow(
                        t.sourceFile(), t.totalThreads(), t.runnableCount(),
                        t.waitingCount() + t.timedWaitingCount(), t.blockedCount(),
                        t.deadlockDetected(), t.hikariWaitCount(), t.jdbcWaitCount(), t.cruzApimWaitCount()
                ))
                .toList();
        boolean deadlock = threads.stream().anyMatch(ThreadDumpSnapshot::deadlockDetected);
        boolean hikari = threads.stream().anyMatch(t -> t.hikariWaitCount() >= 5);
        boolean cruz = threads.stream().anyMatch(t -> t.cruzApimWaitCount() >= 3);

        return new TraceDumpReportView.ThreadAnalysisSection(
                primary.runnableCount(),
                primary.waitingCount() + primary.timedWaitingCount(),
                primary.blockedCount(),
                deadlock ? "CRITICAL — 감지됨" : "없음",
                hikari ? "HIGH — Hikari " + primary.hikariWaitCount() + "+" : "양호",
                cruz ? "MEDIUM — CruzAPIM " + primary.cruzApimWaitCount() + "+" : "특이 없음",
                rows
        );
    }

    private List<TraceDumpReportView.ConclusionRow> buildConclusions(
            List<AnalysisFinding> findings, String oomCategory
    ) {
        Map<String, String> causes = new LinkedHashMap<>();
        causes.put("세션 비대화", "-");
        causes.put("대량 조회 결과 보관", "-");
        causes.put("Cache 누수", "-");
        causes.put("DB Pool 고갈", "-");
        causes.put("외부연계 지연", "-");
        causes.put("Deadlock", "-");
        causes.put("JVM 설정 부적정", "-");

        for (AnalysisFinding f : findings) {
            if (f.severity() == Severity.INFO && f.id().startsWith("INFO")) {
                continue;
            }
            String j = "[" + f.severity() + "] " + f.id() + " " + f.title();
            if (f.id().startsWith("HEAP-003") || f.title().contains("Cache") || f.title().contains("Session")) {
                causes.put("Cache 누수", j);
                causes.put("세션 비대화", j);
            }
            if (f.id().startsWith("THR-002") || f.title().contains("Hikari")) {
                causes.put("DB Pool 고갈", j);
            }
            if (f.id().startsWith("THR-003") || f.title().contains("CruzAPIM")) {
                causes.put("외부연계 지연", j);
            }
            if (f.id().startsWith("THR-001")) {
                causes.put("Deadlock", j);
            }
            if (f.id().startsWith("HEAP") || f.id().startsWith("GC-002")) {
                causes.put("대량 조회 결과 보관", j);
            }
            if (f.id().startsWith("NMT")) {
                causes.put("JVM 설정 부적정", j);
            }
        }
        if ("HEAP_OOM".equals(oomCategory)) {
            causes.put("대량 조회 결과 보관", "Heap OOM — MAT·Histogram·GC 연계");
        }
        return causes.entrySet().stream()
                .map(e -> new TraceDumpReportView.ConclusionRow(e.getKey(), e.getValue()))
                .toList();
    }

    private TraceDumpReportView.ActionPlanSection buildActionPlan(
            List<AnalysisFinding> findings,
            String oomCategory,
            DumpAnalysisIndicators indicators
    ) {
        List<String> urgent = findings.stream()
                .filter(f -> f.severity() == Severity.CRITICAL || f.severity() == Severity.HIGH)
                .map(f -> f.id() + ": " + f.description())
                .limit(6)
                .toList();

        String immediate = urgent.isEmpty()
                ? "트래픽·Heap 사용률 확인, Thread Dump 10초×3 재수집"
                : String.join(" | ", urgent);

        String source = "";
        if (indicators.deadlockFound()) {
            source += "Lock 순서 표준화(customer→campaign→history). ";
        }
        if (indicators.sessionCacheHint() || indicators.queryResultCacheHint()) {
            source += "Static/ConcurrentHashMap 캐시 TTL·최대 크기·eviction. ";
        }
        if (indicators.hikariWaitingThreads() >= 5) {
            source += "Slow SQL·Connection leak·Transaction timeout. ";
        }
        if (source.isBlank()) {
            source = "Finding 근거 파일별 소스·MyBatis·Pool 점검";
        }

        return new TraceDumpReportView.ActionPlanSection(
                immediate,
                "HEAP_OOM".equals(oomCategory)
                        ? "-Xmx/-Xms, HeapDumpOnOOM, G1 로그, Metaspace 한도"
                        : "GC/NMT 로그 옵션, HeapDumpOnOOM",
                source.trim(),
                "증거 보관·재현·Runbook(가이드 10장)",
                "부하·장시간·OOM 시나리오 성능 테스트"
        );
    }

    private String mapOomCategory(String oomCategory) {
        if (oomCategory == null || oomCategory.isBlank() || "UNKNOWN".equals(oomCategory)) {
            return "미분류";
        }
        return switch (oomCategory) {
            case "HEAP_OOM" -> "Heap OOM (java heap space)";
            case "METASPACE_OOM" -> "Metaspace OOM";
            case "DIRECT_BUFFER_OOM" -> "Direct Buffer OOM";
            case "NATIVE_THREAD_OOM" -> "Native Thread OOM";
            case "OS_OOM_KILLER" -> "OS OOM Killer";
            default -> oomCategory;
        };
    }

    private String severityImpact(List<AnalysisFinding> findings, List<OomCorrelation> oomCorrelations) {
        List<AnalysisFinding> urgent = findings.stream()
                .filter(f -> f.severity() == Severity.CRITICAL || f.severity() == Severity.HIGH)
                .toList();
        StringBuilder sb = new StringBuilder();
        if (!urgent.isEmpty()) {
            sb.append("CRITICAL/HIGH Finding ").append(urgent.size()).append("건. ");
            for (int i = 0; i < urgent.size(); i++) {
                if (i > 0) {
                    sb.append(" / ");
                }
                AnalysisFinding f = urgent.get(i);
                sb.append('(').append(i + 1).append(") ")
                        .append(f.id()).append(" [§").append(f.guideSection()).append("] ")
                        .append(f.title()).append(" — ").append(f.description());
            }
        }
        if (!oomCorrelations.isEmpty()) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append("[연계] ");
            for (int i = 0; i < Math.min(3, oomCorrelations.size()); i++) {
                OomCorrelation c = oomCorrelations.get(i);
                if (i > 0) {
                    sb.append(" / ");
                }
                sb.append(c.problemArea()).append("→").append(c.relatedProgram());
            }
        }
        return sb.isEmpty() ? "즉각 CRITICAL/HIGH 미검출 — 수동 Runbook 권장" : sb.toString();
    }

    private String evidenceTypeLabel(EvidenceType type) {
        return switch (type) {
            case THREAD_DUMP -> "Thread Dump";
            case GC_LOG -> "GC Log";
            case HS_ERR -> "hs_err";
            case NMT -> "NMT Summary";
            case CLASS_HISTOGRAM -> "Class Histogram";
            case OS_LOG -> "OS Log";
            case HEAP_DUMP -> "Heap Dump";
            case HEAP_CONSOLE -> "Heap Console";
            case UNKNOWN -> "기타";
        };
    }

    private String evidenceNote(EvidenceFile file) {
        return switch (file.type()) {
            case HEAP_DUMP -> "MAT 수동 — Dominator/Leak Suspects";
            case HEAP_CONSOLE -> "OOM 메시지·Dump 생성 로그";
            case CLASS_HISTOGRAM -> "shallow bytes — retained는 MAT 필요";
            default -> "자동 파싱·규칙 적용";
        };
    }
}
