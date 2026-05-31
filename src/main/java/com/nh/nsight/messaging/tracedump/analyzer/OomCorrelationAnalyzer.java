package com.nh.nsight.messaging.tracedump.analyzer;

import com.nh.nsight.messaging.tracedump.model.ClassHistogramSnapshot;
import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.EvidenceType;
import com.nh.nsight.messaging.tracedump.model.GcLogSnapshot;
import com.nh.nsight.messaging.tracedump.model.HsErrSnapshot;
import com.nh.nsight.messaging.tracedump.model.OomCorrelation;
import com.nh.nsight.messaging.tracedump.model.Severity;
import com.nh.nsight.messaging.tracedump.model.ThreadDumpSnapshot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class OomCorrelationAnalyzer {

    private static final Pattern OOM_LINE = Pattern.compile(
            ".*(OutOfMemoryError|java heap space|gc overhead|metaspace|direct buffer|"
                    + "to-space exhausted|evacuation failure|humongous|full gc|killed process|"
                    + "allocation failure|ergonomics).*",
            Pattern.CASE_INSENSITIVE
    );
    private final ApplicationComponentMapper componentMapper;

    public OomCorrelationAnalyzer(ApplicationComponentMapper componentMapper) {
        this.componentMapper = componentMapper;
    }

    public List<OomCorrelation> analyze(
            String oomCategory,
            List<EvidenceFile> files,
            List<ThreadDumpSnapshot> threads,
            List<GcLogSnapshot> gcLogs,
            List<HsErrSnapshot> hsErrs,
            List<ClassHistogramSnapshot> histograms
    ) {
        List<OomCorrelation> correlations = new ArrayList<>();
        int seq = 1;

        if (oomCategory != null && !"UNKNOWN".equals(oomCategory)) {
            List<String> oomLines = collectOomLogLines(files);
            String logSnippet = oomLines.isEmpty()
                    ? "OOM 유형: " + oomCategory
                    : truncate(oomLines.get(0), 200);
            List<String> stacks = collectApplicationStacks(files);
            correlations.add(new OomCorrelation(
                    "OOM-C-" + (seq++),
                    mapProblemArea(oomCategory),
                    logSnippet,
                    componentMapper.joinPrograms(stacks.isEmpty() ? inferFromHistogram(histograms) : stacks),
                    componentMapper.causeForOomCategory(oomCategory),
                    oomLines.isEmpty() ? "(다중 증거)" : fileNameForLine(files, oomLines.get(0)),
                    Severity.HIGH
            ));
        }

        for (GcLogSnapshot gc : gcLogs) {
            Optional<EvidenceFile> gcFile = findFile(files, gc.sourceFile());
            if (gcFile.isEmpty()) {
                continue;
            }
            List<String> problemLines = extractGcProblemLines(gcFile.get().content());
            for (String line : problemLines) {
                String area = gcProblemArea(line, gc);
                List<String> stacks = componentMapper.extractStackFrames(gcFile.get().content());
                correlations.add(new OomCorrelation(
                        "OOM-C-" + (seq++),
                        area,
                        truncate(line, 220),
                        componentMapper.joinPrograms(stacks.isEmpty()
                                ? List.of(guessProgramFromGc(area)) : stacks),
                        gcCause(line, gc),
                        gc.sourceFile(),
                        gc.fullGcCount() >= 3 || gc.evacuationFailureCount() > 0
                                ? Severity.HIGH : Severity.MEDIUM
                ));
            }
        }

        for (HsErrSnapshot hs : hsErrs) {
            Optional<EvidenceFile> hsFile = findFile(files, hs.sourceFile());
            if (hsFile.isEmpty()) {
                continue;
            }
            String content = hsFile.get().content();
            List<String> oomErr = extractHsErrOomSection(content);
            List<String> stacks = componentMapper.extractStackFrames(content);
            if (!oomErr.isEmpty() || !stacks.isEmpty()) {
                correlations.add(new OomCorrelation(
                        "OOM-C-" + (seq++),
                        "hs_err / JVM Fatal (" + hs.problemCategory() + ")",
                        oomErr.isEmpty() ? hs.signalName() + " " + hs.problematicFrame()
                                : truncate(String.join(" | ", oomErr), 220),
                        componentMapper.joinPrograms(stacks),
                        hsErrCause(hs, stacks),
                        hs.sourceFile(),
                        "JVM_CRASH".equals(hs.problemCategory()) ? Severity.HIGH : Severity.HIGH
                ));
            }
        }

        for (ThreadDumpSnapshot t : threads) {
            Optional<EvidenceFile> tdFile = findFile(files, t.sourceFile());
            if (tdFile.isEmpty()) {
                continue;
            }
            if (t.hikariWaitCount() >= 5 || t.jdbcWaitCount() >= 3 || t.deadlockDetected()
                    || t.cruzApimWaitCount() >= 3) {
                List<String> stacks = pickRelevantStacks(tdFile.get().content(), t);
                correlations.add(new OomCorrelation(
                        "OOM-C-" + (seq++),
                        threadProblemArea(t),
                        threadLogEvidence(t),
                        componentMapper.joinPrograms(stacks),
                        threadCause(t),
                        t.sourceFile(),
                        t.deadlockDetected() || t.hikariWaitCount() >= 5 ? Severity.HIGH : Severity.MEDIUM
                ));
            }
        }

        for (ClassHistogramSnapshot h : histograms) {
            if (h.topEntries().isEmpty()) {
                continue;
            }
            var top = h.topEntries().get(0);
            correlations.add(new OomCorrelation(
                    "OOM-C-" + (seq++),
                    "Heap 객체 분포 (Histogram)",
                    "Top class: " + top.className() + " instances=" + top.instances() + " bytes=" + top.bytes(),
                    componentMapper.mapFromStackOrClass(top.className()),
                    histogramCause(top.className()),
                    h.sourceFile(),
                    Severity.MEDIUM
            ));
        }

        return dedupe(correlations);
    }

    private List<OomCorrelation> dedupe(List<OomCorrelation> list) {
        Map<String, OomCorrelation> byKey = new LinkedHashMap<>();
        for (OomCorrelation c : list) {
            String key = c.problemArea() + "|" + c.evidenceFile() + "|" + c.logEvidence().substring(0, Math.min(40, c.logEvidence().length()));
            byKey.putIfAbsent(key, c);
        }
        return byKey.values().stream()
                .sorted(Comparator.comparingInt(c -> severityOrder(c.severity())))
                .toList();
    }

    private int severityOrder(Severity s) {
        return switch (s) {
            case CRITICAL -> 0;
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
            case INFO -> 4;
        };
    }

    private List<String> collectOomLogLines(List<EvidenceFile> files) {
        return files.stream()
                .filter(f -> f.type() != EvidenceType.HEAP_DUMP)
                .flatMap(f -> f.content().lines())
                .map(String::trim)
                .filter(l -> OOM_LINE.matcher(l).matches())
                .limit(20)
                .toList();
    }

    private List<String> collectApplicationStacks(List<EvidenceFile> files) {
        return files.stream()
                .filter(f -> f.type() == EvidenceType.THREAD_DUMP || f.type() == EvidenceType.HS_ERR
                        || f.type() == EvidenceType.GC_LOG || f.type() == EvidenceType.UNKNOWN)
                .flatMap(f -> componentMapper.extractStackFrames(f.content()).stream())
                .distinct()
                .limit(12)
                .toList();
    }

    private List<String> inferFromHistogram(List<ClassHistogramSnapshot> histograms) {
        return histograms.stream()
                .flatMap(h -> h.topEntries().stream().limit(3))
                .map(ClassHistogramSnapshot.HistogramEntry::className)
                .toList();
    }

    private List<String> extractGcProblemLines(String content) {
        List<String> lines = new ArrayList<>();
        for (String raw : content.split("\n")) {
            String line = raw.trim();
            if (line.isEmpty()) {
                continue;
            }
            String lower = line.toLowerCase(Locale.ROOT);
            if (lower.contains("full gc") || lower.contains("to-space exhausted")
                    || lower.contains("evacuation failure") || lower.contains("humongous")
                    || lower.contains("outofmemory") || lower.contains("java heap")
                    || lower.contains("pause full") || lower.contains("allocation rate")) {
                lines.add(line);
            }
        }
        return lines.stream().limit(5).toList();
    }

    private List<String> extractHsErrOomSection(String content) {
        List<String> lines = new ArrayList<>();
        boolean inException = false;
        for (String raw : content.split("\n")) {
            String line = raw.trim();
            if (line.contains("OutOfMemoryError") || line.contains("java.lang.OutOfMemoryError")) {
                inException = true;
            }
            if (inException) {
                lines.add(line);
                if (lines.size() >= 8) {
                    break;
                }
            }
        }
        return lines;
    }

    private List<String> pickRelevantStacks(String content, ThreadDumpSnapshot t) {
        List<String> frames = new ArrayList<>();
        String block = "";
        for (String raw : content.split("\n")) {
            if (raw.trim().startsWith("\"")) {
                block = raw.toLowerCase(Locale.ROOT);
            }
            String lower = (block + raw).toLowerCase(Locale.ROOT);
            boolean relevant = (t.hikariWaitCount() > 0 && lower.contains("hikari"))
                    || (t.jdbcWaitCount() > 0 && (lower.contains("jdbc") || lower.contains("mybatis")))
                    || (t.deadlockDetected() && (lower.contains("deadlock") || lower.contains("blocked")));
            if (relevant && raw.trim().startsWith("at ")) {
                frames.add(raw.trim().substring(3));
            }
        }
        if (frames.isEmpty()) {
            return componentMapper.extractStackFrames(content).stream().limit(6).toList();
        }
        return frames.stream().distinct().limit(8).toList();
    }

    private String mapProblemArea(String oomCategory) {
        return switch (oomCategory) {
            case "HEAP_OOM" -> "JVM Heap (Old Gen / G1)";
            case "METASPACE_OOM" -> "JVM Metaspace";
            case "DIRECT_BUFFER_OOM" -> "Direct Buffer / Native";
            case "NATIVE_THREAD_OOM" -> "Thread / Native Stack";
            case "OS_OOM_KILLER" -> "OS 메모리 (OOM Killer)";
            case "JVM_CRASH" -> "JVM Native Crash";
            default -> "OOM (" + oomCategory + ")";
        };
    }

    private String gcProblemArea(String line, GcLogSnapshot gc) {
        String lower = line.toLowerCase(Locale.ROOT);
        if (lower.contains("humongous")) {
            return "G1 Humongous Region";
        }
        if (lower.contains("full gc") || lower.contains("old")) {
            return "G1 Old Gen / Full GC";
        }
        if (lower.contains("to-space") || lower.contains("evacuation")) {
            return "G1 Young Gen (Evacuation)";
        }
        if (gc.evacuationFailureCount() > 0) {
            return "G1 Evacuation Failure";
        }
        return "GC 로그 (Heap 압박)";
    }

    private String gcCause(String line, GcLogSnapshot gc) {
        String lower = line.toLowerCase(Locale.ROOT);
        if (lower.contains("humongous")) {
            return "대형 객체(배열/응답) 할당 — 메시지·파일 API 대용량 처리 의심";
        }
        if (lower.contains("full gc") || gc.fullGcCount() >= 3) {
            return "Old Gen 누적 — 장시간 유지 객체·세션·조회 결과 누수";
        }
        if (lower.contains("evacuation") || lower.contains("to-space")) {
            return "Young Gen 회수 실패 — 단시간 대량 할당·GC 속도 부족";
        }
        return "Heap 사용 급증 — 할당 속도 대비 GC 미흡";
    }

    private String guessProgramFromGc(String area) {
        if (area.contains("Humongous")) {
            return "NSIGHT 메시지/파일 API · 대용량 JSON/첨부";
        }
        return "NSIGHT Message Mgmt Service (Spring Boot)";
    }

    private String hsErrCause(HsErrSnapshot hs, List<String> stacks) {
        if (!stacks.isEmpty()) {
            return "hs_err 시점 스택 → " + componentMapper.mapFromStackOrClass(stacks.get(0));
        }
        return componentMapper.causeForOomCategory(hs.problemCategory());
    }

    private String threadProblemArea(ThreadDumpSnapshot t) {
        if (t.deadlockDetected()) {
            return "Thread / Lock (Deadlock)";
        }
        if (t.hikariWaitCount() >= 5) {
            return "DB Connection Pool (Hikari)";
        }
        if (t.jdbcWaitCount() >= 3) {
            return "DB 실행 계층 (JDBC/MyBatis)";
        }
        return "Thread 대기·경합";
    }

    private String threadLogEvidence(ThreadDumpSnapshot t) {
        return "BLOCKED=" + t.blockedCount() + ", WAITING=" + (t.waitingCount() + t.timedWaitingCount())
                + ", Hikari대기=" + t.hikariWaitCount() + ", JDBC대기=" + t.jdbcWaitCount()
                + (t.deadlockDetected() ? ", Deadlock=Y" : "");
    }

    private String threadCause(ThreadDumpSnapshot t) {
        if (t.deadlockDetected()) {
            return "Lock 순서 역전·공유 자원 — 동시 API 호출 시 메시지/트랜잭션 모듈 경합 가능";
        }
        if (t.hikariWaitCount() >= 5) {
            return "커넥션 풀 고갈 — Message/Transaction DB 쿼리 지연·Connection 미반환";
        }
        if (t.jdbcWaitCount() >= 3) {
            return "Slow SQL·대량 조회 — MessageService/MyBatis Mapper 부하";
        }
        return "Thread 지연 — Heap OOM 직전 처리 지연·요청 적체";
    }

    private String histogramCause(String className) {
        String lower = className.toLowerCase(Locale.ROOT);
        if (lower.contains("message")) {
            return "메시지 도메인 객체·리스트 누적 — Message API 조회/등록";
        }
        if (lower.contains("session") || lower.contains("hashmap")) {
            return "세션·캐시 Map 비대화 — Home 로그인·인터셉터 세션";
        }
        if (lower.contains("byte") || lower.contains("char")) {
            return "대용량 바이트/문자 배열 — 전문 캡처·파일·응답 버퍼";
        }
        return "상위 클래스 다수 점유 — MAT Retained 분석으로 할당 경로 확인";
    }

    private Optional<EvidenceFile> findFile(List<EvidenceFile> files, String name) {
        return files.stream().filter(f -> f.fileName().equals(name)).findFirst();
    }

    private String fileNameForLine(List<EvidenceFile> files, String line) {
        return files.stream()
                .filter(f -> f.content().contains(line))
                .map(EvidenceFile::fileName)
                .findFirst()
                .orElse("(다중 증거)");
    }

    private String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        String t = s.replaceAll("\\s+", " ").trim();
        return t.length() <= max ? t : t.substring(0, max - 3) + "...";
    }
}
