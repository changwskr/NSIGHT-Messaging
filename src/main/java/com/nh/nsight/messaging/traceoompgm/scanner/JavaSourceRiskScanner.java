package com.nh.nsight.messaging.traceoompgm.scanner;

import com.nh.nsight.messaging.traceoompgm.collector.SourceFileCollector;
import com.nh.nsight.messaging.traceoompgm.model.OomRiskCategory;
import com.nh.nsight.messaging.traceoompgm.model.OomRiskFinding;
import com.nh.nsight.messaging.traceoompgm.model.OomRiskSeverity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class JavaSourceRiskScanner {

    private static final Pattern SESSION_SET_LARGE = Pattern.compile(
            "session\\.setAttribute\\s*\\([^,]+,\\s*(?!\"|')[A-Za-z]", Pattern.CASE_INSENSITIVE);
    private static final Pattern STATIC_MAP = Pattern.compile(
            "static\\s+(final\\s+)?(Map|HashMap|ConcurrentHashMap|List|ArrayList)", Pattern.CASE_INSENSITIVE);
    private static final Pattern THREAD_LOCAL_SET = Pattern.compile("ThreadLocal.*\\.set\\s*\\(", Pattern.CASE_INSENSITIVE);
    private static final Pattern THREAD_LOCAL_REMOVE = Pattern.compile("\\.remove\\s*\\(\\s*\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern READ_ALL_BYTES = Pattern.compile("Files\\.readAllBytes\\s*\\(", Pattern.CASE_INSENSITIVE);
    private static final Pattern GET_BYTES = Pattern.compile("\\.getBytes\\s*\\(\\s*\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern XSSF = Pattern.compile("XSSFWorkbook|SXSSFWorkbook", Pattern.CASE_INSENSITIVE);
    private static final Pattern UNBOUNDED_QUEUE = Pattern.compile(
            "new\\s+LinkedBlockingQueue\\s*\\(\\s*\\)|new\\s+LinkedBlockingQueue\\s*<", Pattern.CASE_INSENSITIVE);
    private static final Pattern CACHED_POOL = Pattern.compile("Executors\\.newCachedThreadPool", Pattern.CASE_INSENSITIVE);
    private static final Pattern NEW_THREAD = Pattern.compile("new\\s+Thread\\s*\\(", Pattern.CASE_INSENSITIVE);

    public List<OomRiskFinding> scan(SourceFileCollector.ScannedTextFile file) {
        if (file.kind() != SourceFileCollector.FileKind.JAVA) {
            return List.of();
        }
        List<OomRiskFinding> findings = new ArrayList<>();
        String[] lines = file.content().split("\n");
        boolean hasThreadLocalSet = file.content().contains("ThreadLocal");
        boolean hasThreadLocalRemove = THREAD_LOCAL_REMOVE.matcher(file.content()).find();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineNo = i + 1;
            String snippet = truncate(line.trim());

            if (SESSION_SET_LARGE.matcher(line).find()) {
                findings.add(finding("OOM-SESSION-001", OomRiskCategory.SESSION, OomRiskSeverity.CRITICAL,
                        "HttpSession 대용량 객체 저장",
                        "조회 결과 List/DTO를 세션에 보관하면 Heap·세션 비대화로 OOM 위험이 큽니다.",
                        "세션에는 userId/branchId/role 등 최소 식별자만 저장하고 조회 결과는 응답으로만 반환하세요.",
                        file.relativePath(), lineNo, snippet, 90));
            }
            if (STATIC_MAP.matcher(line).find() && !line.contains("private static final Logger")) {
                findings.add(finding("OOM-CACHE-001", OomRiskCategory.CACHE, OomRiskSeverity.HIGH,
                        "static Map/List 필드",
                        "static 컬렉션은 GC Root에 고정되어 Old Gen 누적·캐시 누수로 이어질 수 있습니다.",
                        "Caffeine/Ehcache 등 TTL·maxSize가 있는 캐시로 대체하세요.",
                        file.relativePath(), lineNo, snippet, 75));
            }
            if (THREAD_LOCAL_SET.matcher(line).find()) {
                findings.add(finding("OOM-TL-001", OomRiskCategory.THREADLOCAL, OomRiskSeverity.HIGH,
                        "ThreadLocal.set 사용",
                        "Tomcat 스레드 풀에서 remove 누락 시 객체가 유지될 수 있습니다.",
                        "try-finally에서 ThreadLocal.remove()를 보장하세요.",
                        file.relativePath(), lineNo, snippet, 80));
            }
            if (READ_ALL_BYTES.matcher(line).find()) {
                findings.add(finding("OOM-FILE-002", OomRiskCategory.FILE, OomRiskSeverity.HIGH,
                        "Files.readAllBytes 사용",
                        "파일 전체를 byte[]로 적재하면 Humongous/Heap OOM 위험이 있습니다.",
                        "StreamingResponseBody·InputStream.transferTo로 스트리밍 처리하세요.",
                        file.relativePath(), lineNo, snippet, 85));
            }
            if (GET_BYTES.matcher(line).find()) {
                findings.add(finding("OOM-FILE-001", OomRiskCategory.FILE, OomRiskSeverity.HIGH,
                        "MultipartFile.getBytes() 사용",
                        "업로드 파일 전체를 메모리에 올립니다.",
                        "임시 파일·스트리밍 업로드 또는 크기 제한을 적용하세요.",
                        file.relativePath(), lineNo, snippet, 80));
            }
            if (XSSF.matcher(line).find()) {
                findings.add(finding("OOM-EXCEL-001", OomRiskCategory.EXCEL, OomRiskSeverity.HIGH,
                        "대용량 Excel 워크북 생성",
                        "XSSF/SXSSF는 Heap을 많이 사용합니다.",
                        "SXSSF window size 제한, CSV 스트리밍, 비동기 배치 분할을 검토하세요.",
                        file.relativePath(), lineNo, snippet, 78));
            }
            if (UNBOUNDED_QUEUE.matcher(line).find()) {
                findings.add(finding("OOM-QUEUE-001", OomRiskCategory.QUEUE, OomRiskSeverity.HIGH,
                        "용량 미지정 LinkedBlockingQueue",
                        "무한 큐는 적체 시 Heap OOM으로 이어질 수 있습니다.",
                        "capacity 지정·back-pressure·모니터링을 추가하세요.",
                        file.relativePath(), lineNo, snippet, 72));
            }
            if (CACHED_POOL.matcher(line).find()) {
                findings.add(finding("OOM-THREAD-001", OomRiskCategory.THREAD, OomRiskSeverity.MEDIUM,
                        "newCachedThreadPool 사용",
                        "스레드 수 무제한 증가로 Native/Heap 압박 가능.",
                        "고정 크기 ThreadPoolExecutor를 사용하세요.",
                        file.relativePath(), lineNo, snippet, 65));
            }
            if (NEW_THREAD.matcher(line).find() && !line.contains("//")) {
                findings.add(finding("OOM-THREAD-002", OomRiskCategory.THREAD, OomRiskSeverity.MEDIUM,
                        "요청 단위 new Thread 생성",
                        "Thread 폭증·Native OOM 위험.",
                        "@Async·공유 Executor·작업 큐로 대체하세요.",
                        file.relativePath(), lineNo, snippet, 60));
            }
        }

        if (hasThreadLocalSet && !hasThreadLocalRemove) {
            findings.add(finding("OOM-TL-002", OomRiskCategory.THREADLOCAL, OomRiskSeverity.HIGH,
                    "ThreadLocal.remove 미검출 (파일 단위)",
                    "set은 있으나 remove/finally 패턴이 보이지 않습니다.",
                    "A.4 패턴: try { set } finally { remove } 적용.",
                    file.relativePath(), 0, "ThreadLocal 사용 파일", 70));
        }
        return findings;
    }

    private OomRiskFinding finding(
            String ruleId, OomRiskCategory cat, OomRiskSeverity sev,
            String title, String desc, String rec,
            String path, int line, String snippet, int score
    ) {
        return new OomRiskFinding(ruleId, cat, sev, title, desc, rec, path, line, snippet, score);
    }

    private String truncate(String s) {
        return s.length() > 160 ? s.substring(0, 157) + "..." : s;
    }
}
