package com.nh.nsight.messaging.traceoompgm.service;

import com.nh.nsight.messaging.traceoompgm.collector.SourceFileCollector;
import com.nh.nsight.messaging.traceoompgm.config.OomInspectorProperties;
import com.nh.nsight.messaging.traceoompgm.model.OomRiskFinding;
import com.nh.nsight.messaging.traceoompgm.model.OomScanReport;
import com.nh.nsight.messaging.traceoompgm.report.OomScanReportBuilder;
import com.nh.nsight.messaging.traceoompgm.scanner.JavaSourceRiskScanner;
import com.nh.nsight.messaging.traceoompgm.scanner.MapperXmlRiskScanner;
import com.nh.nsight.messaging.traceoompgm.scanner.YamlConfigRiskScanner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class OomInspectorScanService {

    private final OomInspectorProperties properties;
    private final SourceFileCollector fileCollector;
    private final JavaSourceRiskScanner javaScanner;
    private final MapperXmlRiskScanner mapperScanner;
    private final YamlConfigRiskScanner configScanner;
    private final OomScanReportBuilder reportBuilder;

    public OomInspectorScanService(
            OomInspectorProperties properties,
            SourceFileCollector fileCollector,
            JavaSourceRiskScanner javaScanner,
            MapperXmlRiskScanner mapperScanner,
            YamlConfigRiskScanner configScanner,
            OomScanReportBuilder reportBuilder
    ) {
        this.properties = properties;
        this.fileCollector = fileCollector;
        this.javaScanner = javaScanner;
        this.mapperScanner = mapperScanner;
        this.configScanner = configScanner;
        this.reportBuilder = reportBuilder;
    }

    public OomScanReport scan(
            String projectName,
            String sourceRoot,
            String mapperRoot,
            String configPath,
            boolean failOnCritical
    ) throws IOException {
        Path src = Path.of(sourceRoot != null && !sourceRoot.isBlank()
                ? sourceRoot : properties.getDefaultSourceRoot()).toAbsolutePath().normalize();
        Path mapper = Path.of(mapperRoot != null && !mapperRoot.isBlank()
                ? mapperRoot : properties.getDefaultMapperRoot()).toAbsolutePath().normalize();
        Path config = Path.of(configPath != null && !configPath.isBlank()
                ? configPath : properties.getDefaultConfigPath()).toAbsolutePath().normalize();

        if (!Files.exists(src)) {
            throw new IllegalArgumentException("소스 경로가 없습니다: " + src);
        }

        List<SourceFileCollector.ScannedTextFile> files = fileCollector.collect(src, mapper, config);
        List<OomRiskFinding> findings = new ArrayList<>();
        for (SourceFileCollector.ScannedTextFile file : files) {
            findings.addAll(javaScanner.scan(file));
            findings.addAll(mapperScanner.scan(file));
            findings.addAll(configScanner.scan(file));
        }

        return reportBuilder.build(
                projectName != null && !projectName.isBlank() ? projectName : "nsight-message-mgmt-service",
                src.toString(),
                Files.exists(config) ? config.toString() : "",
                Files.exists(mapper) ? mapper.toString() : "",
                files.size(),
                findings,
                failOnCritical
        );
    }

    public OomScanReport scanDefaultProject() throws IOException {
        return scan(
                "nsight-message-mgmt-service",
                properties.getDefaultSourceRoot(),
                properties.getDefaultMapperRoot(),
                properties.getDefaultConfigPath(),
                properties.isFailOnCritical()
        );
    }

    public boolean evaluateGate(OomScanReport report, String failOn) {
        if ("CRITICAL".equalsIgnoreCase(failOn)) {
            return report.gatePassed();
        }
        if ("HIGH".equalsIgnoreCase(failOn)) {
            long high = report.findingsBySeverity().getOrDefault("HIGH", 0L);
            long critical = report.findingsBySeverity().getOrDefault("CRITICAL", 0L);
            return critical == 0 && high == 0;
        }
        return true;
    }
}
