package com.nh.nsight.messaging.traceoompgm.scanner;

import com.nh.nsight.messaging.traceoompgm.collector.SourceFileCollector;
import com.nh.nsight.messaging.traceoompgm.model.OomRiskCategory;
import com.nh.nsight.messaging.traceoompgm.model.OomRiskFinding;
import com.nh.nsight.messaging.traceoompgm.model.OomRiskSeverity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MapperXmlRiskScanner {

    private static final Pattern SELECT_LIST = Pattern.compile(
            "<select\\s+[^>]*id\\s*=\\s*\"([^\"]+)\"[^>]*>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern PAGING_HINT = Pattern.compile(
            "limit|rownum|offset|fetch\\s+first|pageSize|ROW_NUMBER",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern CLOB_BLOB = Pattern.compile("jdbcType\\s*=\\s*\"(CLOB|BLOB)\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern FETCH_SIZE = Pattern.compile("fetchSize\\s*=", Pattern.CASE_INSENSITIVE);

    public List<OomRiskFinding> scan(SourceFileCollector.ScannedTextFile file) {
        if (file.kind() != SourceFileCollector.FileKind.MAPPER_XML) {
            return List.of();
        }
        List<OomRiskFinding> findings = new ArrayList<>();
        String content = file.content();
        String lower = content.toLowerCase(Locale.ROOT);

        Matcher m = SELECT_LIST.matcher(content);
        while (m.find()) {
            String id = m.group(1);
            String block = extractSelectBlock(content, m.start());
            if (!PAGING_HINT.matcher(block).find()) {
                findings.add(finding("OOM-SQL-001", OomRiskSeverity.HIGH,
                        "페이징 없는 SELECT: " + id,
                        "대량 조회 시 전체 결과가 Heap에 적재됩니다.",
                        "PageHelper/ROWNUM/LIMIT·조회 건수 상한을 적용하세요.",
                        file.relativePath(), truncate(block), 88));
            }
            if (!FETCH_SIZE.matcher(block).find() && block.length() > 200) {
                findings.add(finding("OOM-SQL-002", OomRiskSeverity.MEDIUM,
                        "fetchSize 미설정 SQL: " + id,
                        "대량 fetch 시 메모리 스파이크 가능.",
                        "mapper에 fetchSize=\"500\" 등 설정을 추가하세요.",
                        file.relativePath(), truncate(block), 65));
            }
        }

        if (CLOB_BLOB.matcher(content).find()) {
            findings.add(finding("OOM-SQL-003", OomRiskSeverity.HIGH,
                    "CLOB/BLOB 대용량 컬럼 조회",
                    "LOB 전체 로딩은 Heap·Direct Buffer 압박.",
                    "스트리밍 Reader·필요 컬럼만·페이징 조회로 제한하세요.",
                    file.relativePath(), "jdbcType CLOB/BLOB", 82));
        }
        return findings;
    }

    private String extractSelectBlock(String content, int start) {
        int end = Math.min(content.length(), start + 2500);
        return content.substring(start, end);
    }

    private OomRiskFinding finding(
            String ruleId, OomRiskSeverity sev, String title, String desc, String rec,
            String path, String snippet, int score
    ) {
        return new OomRiskFinding(ruleId, OomRiskCategory.SQL, sev, title, desc, rec, path, 0, truncate(snippet), score);
    }

    private String truncate(String s) {
        return s.length() > 200 ? s.substring(0, 197) + "..." : s;
    }
}
