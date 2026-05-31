package com.traceoompgm.scanner;

import com.traceoompgm.collector.SourceFileCollector;
import com.traceoompgm.model.OomRiskCategory;
import com.traceoompgm.model.OomRiskFinding;
import com.traceoompgm.model.OomRiskSeverity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class YamlConfigRiskScanner {

    private static final Pattern HIKARI_MAX = Pattern.compile("maximum-pool-size:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOMCAT_THREADS = Pattern.compile("max:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    public List<OomRiskFinding> scan(SourceFileCollector.ScannedTextFile file) {
        if (file.kind() != SourceFileCollector.FileKind.YAML
                && file.kind() != SourceFileCollector.FileKind.PROPERTIES) {
            return List.of();
        }
        List<OomRiskFinding> findings = new ArrayList<>();
        String content = file.content();

        Matcher hikari = HIKARI_MAX.matcher(content);
        while (hikari.find()) {
            int size = Integer.parseInt(hikari.group(1));
            if (size >= 100) {
                findings.add(configFinding("OOM-CONFIG-001", OomRiskSeverity.MEDIUM,
                        "Hikari maximum-pool-size=" + size,
                        "Pool·Connection 메모리와 DB 부하가 증가합니다.",
                        "NSIGHT 8core 기준 Pool 30~50, leakDetectionThreshold 활성화 검토.",
                        file.relativePath(), hikari.group(0), 55));
            }
        }

        if (content.toLowerCase(Locale.ROOT).contains("tomcat:")) {
            Matcher tomcat = TOMCAT_THREADS.matcher(content);
            while (tomcat.find()) {
                int max = Integer.parseInt(tomcat.group(1));
                if (max >= 300) {
                    findings.add(configFinding("OOM-CONFIG-002", OomRiskSeverity.MEDIUM,
                            "Tomcat max threads=" + max,
                            "Thread·Stack Native 메모리 증가.",
                            "TPS·응답시간에 맞춰 스레드 상한 재조정.",
                            file.relativePath(), tomcat.group(0), 50));
                }
            }
        }

        if (!content.contains("max-file-size") && content.contains("multipart:")) {
            findings.add(configFinding("OOM-CONFIG-003", OomRiskSeverity.LOW,
                    "multipart 업로드 한도 확인 필요",
                    "대용량 업로드는 Heap OOM을 유발할 수 있습니다.",
                    "max-file-size·max-request-size를 명시하세요.",
                    file.relativePath(), "multipart", 40));
        }
        return findings;
    }

    private OomRiskFinding configFinding(
            String ruleId, OomRiskSeverity sev, String title, String desc, String rec,
            String path, String snippet, int score
    ) {
        return new OomRiskFinding(ruleId, OomRiskCategory.CONFIG, sev, title, desc, rec, path, 0, snippet, score);
    }
}
