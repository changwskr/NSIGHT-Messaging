package com.nh.nsight.messaging.tracedump.analyzer;

import com.nh.nsight.messaging.tracedump.model.AnalysisFinding;
import com.nh.nsight.messaging.tracedump.model.ClassHistogramSnapshot;
import com.nh.nsight.messaging.tracedump.model.DumpAnalysisIndicators;
import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.EvidenceType;
import com.nh.nsight.messaging.tracedump.model.GcLogSnapshot;
import com.nh.nsight.messaging.tracedump.model.HsErrSnapshot;
import com.nh.nsight.messaging.tracedump.model.NmtSnapshot;
import com.nh.nsight.messaging.tracedump.model.Severity;
import com.nh.nsight.messaging.tracedump.model.ThreadDumpSnapshot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * NSIGHT JVM Dump 분석 규칙 엔진 (개발용 명세 §13 기준).
 */
@Component
public class TraceDumpRuleEngine {

    private static final int HIKARI_WAIT_THRESHOLD = 5;
    private static final int CRUZ_APIM_WAIT_THRESHOLD = 3;
    private static final int FULL_GC_THRESHOLD = 5;

    public List<AnalysisFinding> evaluate(
            List<EvidenceFile> files,
            List<ThreadDumpSnapshot> threads,
            List<GcLogSnapshot> gcLogs,
            List<HsErrSnapshot> hsErrs,
            List<NmtSnapshot> nmts,
            List<ClassHistogramSnapshot> histograms,
            String oomCategory,
            DumpAnalysisIndicators indicators
    ) {
        List<AnalysisFinding> findings = new ArrayList<>();

        evaluateHeapRules(files, oomCategory, indicators, histograms, findings);
        evaluateThreadRules(threads, findings);
        evaluateGcRules(gcLogs, indicators, findings);
        evaluateNmtRules(nmts, findings);
        evaluateHsErrRules(hsErrs, findings);
        evaluateOomCategory(oomCategory, findings);

        for (EvidenceFile file : files) {
            if (file.type() == EvidenceType.HEAP_DUMP) {
                findings.add(finding(
                        "HEAP-010-" + file.fileName(),
                        "Heap",
                        "Heap Dump 수집됨 (MAT 분석 필요)",
                        "Eclipse MAT Dominator Tree / Leak Suspects / GC Roots로 Retained Heap 확인.",
                        Severity.INFO,
                        "4",
                        file.fileName()
                ));
            }
        }

        if (findings.isEmpty()) {
            findings.add(finding(
                    "INFO-001",
                    "General",
                    "자동 규칙에서 명확한 이상 패턴 미검출",
                    "증거 파일 수: " + files.size() + ". MAT/Runbook(가이드 10장) 추가 분석 권장.",
                    Severity.INFO,
                    "10",
                    "thread=" + threads.size() + ", gc=" + gcLogs.size()
            ));
        }

        return findings;
    }

    private void evaluateOomCategory(String oomCategory, List<AnalysisFinding> findings) {
        if (oomCategory == null || oomCategory.isBlank() || "UNKNOWN".equals(oomCategory)) {
            return;
        }
        findings.add(finding(
                "OOM-001",
                "OOM",
                "OOM 유형 분류: " + oomCategory,
                "가이드 1.2 — 유형별 수집·분석 경로 분기.",
                Severity.HIGH,
                "1.2",
                oomCategory
        ));
        if ("OS_OOM_KILLER".equals(oomCategory)) {
            findings.add(finding(
                    "OS-001",
                    "OS",
                    "OS OOM Killer 가능성",
                    "dmesg/journalctl Killed process 확인. JVM Heap 외 RSS·Native 점검.",
                    Severity.HIGH,
                    "8.1",
                    "OS 로그 키워드"
            ));
        }
    }

    private void evaluateHeapRules(
            List<EvidenceFile> files,
            String oomCategory,
            DumpAnalysisIndicators indicators,
            List<ClassHistogramSnapshot> histograms,
            List<AnalysisFinding> findings
    ) {
        boolean heapSpaceInLog = files.stream()
                .filter(f -> f.type() == EvidenceType.HEAP_CONSOLE || f.type() == EvidenceType.HS_ERR
                        || f.type() == EvidenceType.UNKNOWN)
                .anyMatch(f -> f.content().toLowerCase(Locale.ROOT).contains("java heap space"));

        if (heapSpaceInLog || "HEAP_OOM".equals(oomCategory)) {
            findings.add(finding(
                    "HEAP-001",
                    "Heap",
                    "Java Heap OOM (java heap space)",
                    "Heap Console/hs_err에서 OOM 메시지 확인. Heap Dump·Histogram 교차 분석.",
                    Severity.HIGH,
                    "4",
                    "HEAP-001: console/hs_err 키워드"
            ));
        }

        if (indicators.heapNotReducedAfterFullGc() || indicators.oldRegionNotReduced()) {
            findings.add(finding(
                    "HEAP-002",
                    "Heap",
                    "Full GC 후 Heap/Old Region 회수 실패",
                    "Live Object Retention 의심 — MAT Retained Heap·GC Root 경로 확인.",
                    Severity.HIGH,
                    "6",
                    "GC-002 연동: Old/Heap before->after 동일"
            ));
        }

        if (indicators.sessionCacheHint()) {
            findings.add(finding(
                    "HEAP-003",
                    "Heap",
                    "SESSION_CACHE / Session 관련 문자열 노출",
                    "세션·Single View 캐시 비대화 의심. 세션 TTL·최소 필드 정책 점검.",
                    Severity.HIGH,
                    "4.2",
                    "SESSION_CACHE, NsightSession"
            ));
        }

        if (indicators.queryResultCacheHint()) {
            findings.add(finding(
                    "HEAP-003b",
                    "Heap",
                    "QUERY_RESULT_CACHE 문자열 노출",
                    "대량 조회 결과 캐시 의심. TTL·최대 건수·eviction 정책 점검.",
                    Severity.HIGH,
                    "4.2",
                    "QUERY_RESULT_CACHE"
            ));
        }

        for (ClassHistogramSnapshot h : histograms) {
            if (h.topEntries().isEmpty()) {
                continue;
            }
            var top = h.topEntries().get(0);
            findings.add(finding(
                    "HEAP-004-" + h.sourceFile(),
                    "Heap",
                    "Histogram Top: " + top.className(),
                    "인스턴스 " + top.instances() + ", bytes " + top.bytes()
                            + " — retained/GC Root는 MAT 필요 (Histogram만으로 확정 불가).",
                    Severity.INFO,
                    "4",
                    h.sourceFile()
            ));
            String cn = top.className().toLowerCase(Locale.ROOT);
            if (cn.contains("byte") || cn.contains("[b") || cn.contains("string")
                    || cn.contains("arraylist") || cn.contains("dto")) {
                findings.add(finding(
                        "HEAP-004b-" + h.sourceFile(),
                        "Heap",
                        "대용량 바이트/문자열/컬렉션 상위 노출",
                        "대량 조회·응답 버퍼·JSON 처리 패턴 점검. Paging/Fetch Size 제한.",
                        Severity.MEDIUM,
                        "4",
                        top.className()
                ));
            }
        }
    }

    private void evaluateThreadRules(List<ThreadDumpSnapshot> threads, List<AnalysisFinding> findings) {
        for (ThreadDumpSnapshot t : threads) {
            if (t.deadlockDetected()) {
                findings.add(finding(
                        "THR-001-" + t.sourceFile(),
                        "Thread",
                        "Java-level Deadlock",
                        "모든 관련 스레드 정지 가능. Lock 획득 순서 표준화(customer→campaign→history).",
                        Severity.CRITICAL,
                        "5",
                        t.sourceFile()
                ));
            }
            if (t.hikariWaitCount() >= HIKARI_WAIT_THRESHOLD) {
                findings.add(finding(
                        "THR-002-" + t.sourceFile(),
                        "Thread",
                        "Hikari Pool 대기 Thread >= " + HIKARI_WAIT_THRESHOLD,
                        "DB Pool 고갈·Slow SQL·Connection 미반환. Pool/Transaction/SQL 튜닝.",
                        Severity.HIGH,
                        "5.2",
                        "Hikari대기=" + t.hikariWaitCount()
                ));
            }
            if (t.cruzApimWaitCount() >= CRUZ_APIM_WAIT_THRESHOLD) {
                findings.add(finding(
                        "THR-003-" + t.sourceFile(),
                        "Thread",
                        "CruzAPIM 대기/Timeout Thread >= " + CRUZ_APIM_WAIT_THRESHOLD,
                        "외부 연계 Read Timeout. Circuit Breaker·Bulkhead·비동기 전환 검토.",
                        Severity.MEDIUM,
                        "5.2",
                        "CruzAPIM대기=" + t.cruzApimWaitCount()
                ));
            }
            if (t.totalThreads() > 0 && t.blockedCount() >= 2) {
                findings.add(finding(
                        "THR-004-" + t.sourceFile(),
                        "Thread",
                        "BLOCKED Thread " + t.blockedCount() + "건",
                        "synchronized/Lock 경합. Deadlock과 함께 Lock 순서 점검.",
                        Severity.HIGH,
                        "5.2",
                        t.sourceFile()
                ));
            }
        }
    }

    private void evaluateGcRules(
            List<GcLogSnapshot> gcLogs,
            DumpAnalysisIndicators indicators,
            List<AnalysisFinding> findings
    ) {
        for (GcLogSnapshot g : gcLogs) {
            if (g.fullGcCount() >= FULL_GC_THRESHOLD) {
                findings.add(finding(
                        "GC-001-" + g.sourceFile(),
                        "GC",
                        "Full GC 반복 >= " + FULL_GC_THRESHOLD + " (" + g.fullGcCount() + ")",
                        "Heap 압박·Old Gen 누적. MAT·Histogram 연계.",
                        Severity.HIGH,
                        "6",
                        g.sourceFile()
                ));
            }
            if (g.heapUnchangedAfterFullGcCount() > 0 || g.oldRegionUnchangedCount() > 0) {
                findings.add(finding(
                        "GC-002-" + g.sourceFile(),
                        "GC",
                        "Full GC 후 Heap/Old Region 용량 미감소",
                        "Live object retention (HEAP-002). 61M->61M, Old regions N->N 패턴.",
                        Severity.HIGH,
                        "6.2",
                        "heapUnchanged=" + g.heapUnchangedAfterFullGcCount()
                                + ", oldUnchanged=" + g.oldRegionUnchangedCount()
                ));
            }
            if (g.softReferenceCompactionCount() > 0) {
                findings.add(finding(
                        "GC-003-" + g.sourceFile(),
                        "GC",
                        "Soft Reference 정리 시도 (maximal full compaction)",
                        "JVM이 메모리 회수를 시도했으나 객체 유지 — retention 강함.",
                        Severity.HIGH,
                        "6",
                        g.sourceFile()
                ));
            }
            if (g.evacuationFailureCount() > 0) {
                findings.add(finding(
                        "GC-004-" + g.sourceFile(),
                        "GC",
                        "Evacuation Failure / To-space exhausted",
                        "G1 Young Gen 압박·대형 객체 할당.",
                        Severity.HIGH,
                        "6.2",
                        g.sourceFile()
                ));
            }
            if (g.maxPauseMs() >= 1000) {
                findings.add(finding(
                        "GC-005-" + g.sourceFile(),
                        "GC",
                        "GC Pause 1초 이상 (max " + g.maxPauseMs() + "ms)",
                        "응답 지연 기여 가능.",
                        Severity.MEDIUM,
                        "6.2",
                        g.sourceFile()
                ));
            }
        }
        if (indicators.fullGcCount() >= FULL_GC_THRESHOLD && findings.stream().noneMatch(f -> f.id().startsWith("GC-001"))) {
            findings.add(finding(
                    "GC-001-AGG",
                    "GC",
                    "Full GC 누적 >= " + FULL_GC_THRESHOLD,
                    "여러 GC 로그 합산 " + indicators.fullGcCount() + "회.",
                    Severity.HIGH,
                    "6",
                    "aggregated"
            ));
        }
    }

    private void evaluateNmtRules(List<NmtSnapshot> nmts, List<AnalysisFinding> findings) {
        for (NmtSnapshot n : nmts) {
            for (NmtSnapshot.NmtLine line : n.topLines()) {
                if ("Thread".equalsIgnoreCase(line.category()) && line.committedKb() > 512_000) {
                    findings.add(finding(
                            "NMT-001-" + n.sourceFile(),
                            "Native",
                            "NMT Thread 영역 과다 (" + line.committedKb() + " KB)",
                            "Thread 수·Xss·Pool 상한 점검 (THR/NMT 연계).",
                            Severity.MEDIUM,
                            "7",
                            n.sourceFile()
                    ));
                }
                if ("Metaspace".equalsIgnoreCase(line.category()) && line.committedKb() > 100_000) {
                    findings.add(finding(
                            "NMT-002-" + n.sourceFile(),
                            "Native",
                            "Metaspace committed 증가 (" + line.committedKb() + " KB)",
                            "ClassLoader/동적 클래스 로딩 점검.",
                            Severity.MEDIUM,
                            "7.2",
                            n.sourceFile()
                    ));
                }
            }
        }
    }

    private void evaluateHsErrRules(List<HsErrSnapshot> hsErrs, List<AnalysisFinding> findings) {
        for (HsErrSnapshot h : hsErrs) {
            if ("JVM_CRASH".equals(h.problemCategory())) {
                findings.add(finding(
                        "HS-001-" + h.sourceFile(),
                        "Crash",
                        "JVM Fatal / Native Crash",
                        "hs_err·Core Dump·Native 라이브러리 점검.",
                        Severity.CRITICAL,
                        "8",
                        h.signalName() + " " + h.problematicFrame()
                ));
            }
        }
    }

    private AnalysisFinding finding(
            String id, String category, String title, String description,
            Severity severity, String guideSection, String evidence
    ) {
        return new AnalysisFinding(id, category, title, description, severity, guideSection, evidence);
    }
}
