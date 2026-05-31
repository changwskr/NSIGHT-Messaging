package com.nh.nsight.messaging.tracedump.analyzer;

import com.nh.nsight.messaging.tracedump.model.AnalysisFinding;
import com.nh.nsight.messaging.tracedump.model.ClassHistogramSnapshot;
import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.GcLogSnapshot;
import com.nh.nsight.messaging.tracedump.model.HsErrSnapshot;
import com.nh.nsight.messaging.tracedump.model.NmtSnapshot;
import com.nh.nsight.messaging.tracedump.model.Severity;
import com.nh.nsight.messaging.tracedump.model.ThreadDumpSnapshot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TraceDumpRuleEngine {

    public List<AnalysisFinding> evaluate(
            List<EvidenceFile> files,
            List<ThreadDumpSnapshot> threads,
            List<GcLogSnapshot> gcLogs,
            List<HsErrSnapshot> hsErrs,
            List<NmtSnapshot> nmts,
            List<ClassHistogramSnapshot> histograms,
            String oomCategory
    ) {
        List<AnalysisFinding> findings = new ArrayList<>();

        if (oomCategory != null && !oomCategory.isBlank() && !"UNKNOWN".equals(oomCategory)) {
            findings.add(finding(
                    "OOM-001",
                    "OOM",
                    "OOM 유형 분류: " + oomCategory,
                    "가이드 1.2 1단계 — OOM 유형에 따라 수집·분석 경로가 달라집니다.",
                    Severity.HIGH,
                    "1.2",
                    "분류 결과: " + oomCategory
            ));
        }

        for (ThreadDumpSnapshot t : threads) {
            if (t.deadlockDetected()) {
                findings.add(finding(
                        "TH-001-" + t.sourceFile(),
                        "Thread",
                        "Java-level Deadlock 감지",
                        "Thread Dump에서 deadlock 문구가 확인되었습니다. Lock 순서·공유 자원을 점검하세요.",
                        Severity.HIGH,
                        "5",
                        t.sourceFile()
                ));
            }
            if (t.totalThreads() > 0 && t.blockedCount() * 100 / t.totalThreads() >= 20) {
                findings.add(finding(
                        "TH-002-" + t.sourceFile(),
                        "Thread",
                        "BLOCKED Thread 비율 높음 (" + t.blockedCount() + "/" + t.totalThreads() + ")",
                        "synchronized/Lock 경합 가능성. 가이드 5장 BLOCKED 점검.",
                        Severity.HIGH,
                        "5.2",
                        t.sourceFile() + " state=" + t.stateCounts()
                ));
            }
            if (t.hikariWaitCount() >= 3) {
                findings.add(finding(
                        "TH-003-" + t.sourceFile(),
                        "Thread",
                        "Hikari Connection Pool 대기 다수 (" + t.hikariWaitCount() + ")",
                        "DB Pool 고갈·Slow SQL·Connection 누수 의심. Pool 크기·SQL·Timeout 확인.",
                        Severity.HIGH,
                        "5.2",
                        t.sourceFile()
                ));
            }
            if (t.jdbcWaitCount() >= 3) {
                findings.add(finding(
                        "TH-004-" + t.sourceFile(),
                        "Thread",
                        "JDBC 실행 대기 Thread 다수 (" + t.jdbcWaitCount() + ")",
                        "RDW SQL 지연·실행 계획·Fetch Size 점검.",
                        Severity.MEDIUM,
                        "5.2",
                        t.sourceFile()
                ));
            }
        }

        for (GcLogSnapshot g : gcLogs) {
            if (g.fullGcCount() >= 3) {
                findings.add(finding(
                        "GC-001-" + g.sourceFile(),
                        "GC",
                        "Full GC 반복 (" + g.fullGcCount() + "회 언급)",
                        "Old Gen 누적·Humongous·메모리 누수 가능. Heap Dump MAT 분석 권장.",
                        Severity.HIGH,
                        "6",
                        g.sourceFile()
                ));
            }
            if (g.maxPauseMs() >= 1000) {
                findings.add(finding(
                        "GC-002-" + g.sourceFile(),
                        "GC",
                        "GC Pause 1초 이상 (max " + g.maxPauseMs() + "ms)",
                        "p95 3초 목표 대비 GC가 응답 지연에 기여할 수 있습니다.",
                        Severity.MEDIUM,
                        "6.2",
                        g.sourceFile()
                ));
            }
            if (g.evacuationFailureCount() > 0) {
                findings.add(finding(
                        "GC-003-" + g.sourceFile(),
                        "GC",
                        "Evacuation Failure / To-space exhausted",
                        "G1 Heap 압박·대형 객체 할당 패턴 점검.",
                        Severity.HIGH,
                        "6.2",
                        g.sourceFile()
                ));
            }
            if (g.humongousMentionCount() >= 5) {
                findings.add(finding(
                        "GC-004-" + g.sourceFile(),
                        "GC",
                        "Humongous allocation 다수",
                        "대용량 배열·다운로드·JSON 처리 등 대형 객체 생성 검토.",
                        Severity.MEDIUM,
                        "6",
                        g.sourceFile()
                ));
            }
        }

        for (HsErrSnapshot h : hsErrs) {
            if ("JVM_CRASH".equals(h.problemCategory())) {
                findings.add(finding(
                        "HS-001-" + h.sourceFile(),
                        "Crash",
                        "JVM Fatal / Native Crash",
                        "hs_err 분석 및 Core Dump·Native 라이브러리 점검.",
                        Severity.HIGH,
                        "8",
                        h.signalName() + " " + h.problematicFrame()
                ));
            }
        }

        if ("OS_OOM_KILLER".equals(oomCategory)) {
            findings.add(finding(
                    "OS-001",
                    "OS",
                    "OS OOM Killer 가능성",
                    "dmesg/journalctl에서 Killed process 확인. JVM Heap 외 RSS·Native 점검.",
                    Severity.HIGH,
                    "8.1",
                    "OS 로그 키워드 매칭"
            ));
        }

        for (NmtSnapshot n : nmts) {
            for (NmtSnapshot.NmtLine line : n.topLines()) {
                if ("Thread".equalsIgnoreCase(line.category()) && line.committedKb() > 512_000) {
                    findings.add(finding(
                            "NMT-001-" + n.sourceFile(),
                            "Native",
                            "NMT Thread 영역 과다 (" + line.committedKb() + " KB)",
                            "Thread 수·Xss·Pool 상한 점검.",
                            Severity.MEDIUM,
                            "7",
                            n.sourceFile()
                    ));
                }
                if ("Internal".equalsIgnoreCase(line.category()) && line.committedKb() > 1_048_576) {
                    findings.add(finding(
                            "NMT-002-" + n.sourceFile(),
                            "Native",
                            "NMT Internal 영역 과다",
                            "Direct Buffer·JNI 등 Native 사용량 점검.",
                            Severity.MEDIUM,
                            "7.2",
                            n.sourceFile()
                    ));
                }
            }
        }

        for (ClassHistogramSnapshot h : histograms) {
            if (!h.topEntries().isEmpty()) {
                var top = h.topEntries().get(0);
                findings.add(finding(
                        "HIST-001-" + h.sourceFile(),
                        "Heap",
                        "Histogram Top: " + top.className(),
                        "인스턴스 " + top.instances() + ", 바이트 " + top.bytes()
                                + ". MAT Retained Heap 분석으로 누수 여부 확인.",
                        Severity.INFO,
                        "4",
                        h.sourceFile()
                ));
                String className = top.className().toLowerCase();
                if (className.contains("session") || className.contains("hashmap")
                        || className.contains("concurrenthashmap")) {
                    findings.add(finding(
                            "HIST-002-" + h.sourceFile(),
                            "Heap",
                            "Session/Cache 관련 클래스 상위 노출",
                            "세션·캐시·ThreadLocal 정리 정책 점검 (가이드 4.2).",
                            Severity.MEDIUM,
                            "4.2",
                            top.className()
                    ));
                }
            }
        }

        if (findings.isEmpty()) {
            findings.add(finding(
                    "INFO-001",
                    "General",
                    "자동 규칙에서 명확한 이상 패턴 미검출",
                    "증거 파일 수: " + files.size()
                            + ". MAT/수동 Runbook(가이드 10장) 추가 분석을 권장합니다.",
                    Severity.INFO,
                    "10",
                    "thread=" + threads.size() + ", gc=" + gcLogs.size()
            ));
        }

        return findings;
    }

    private AnalysisFinding finding(
            String id, String category, String title, String description,
            Severity severity, String guideSection, String evidence
    ) {
        return new AnalysisFinding(id, category, title, description, severity, guideSection, evidence);
    }
}
