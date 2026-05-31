package com.nh.nsight.messaging.operations.service;

import com.nh.nsight.messaging.common.log.MessageEnvelopeFileService;
import com.nh.nsight.messaging.config.FileStorageProperties;
import com.nh.nsight.messaging.config.MessageEnvelopeProperties;
import com.nh.nsight.messaging.config.TraceDumpProperties;
import com.nh.nsight.messaging.file.service.FileService;
import com.nh.nsight.messaging.traceoompgm.config.OomInspectorProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperationsInfoService {

    private final MessageEnvelopeFileService messageEnvelopeFileService;
    private final MessageEnvelopeProperties messageEnvelopeProperties;
    private final FileStorageProperties fileStorageProperties;
    private final TraceDumpProperties traceDumpProperties;
    private final OomInspectorProperties oomInspectorProperties;
    private final Environment environment;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${nsight.ap-id:}")
    private String apId;

    @Value("${nsight.cruzapim.base-url:}")
    private String cruzApimBaseUrl;

    @Value("${logging.file.path:./logs}")
    private String loggingPath;

    @Value("${server.port:8080}")
    private int serverPort;

    public OperationsInfoService(
            MessageEnvelopeFileService messageEnvelopeFileService,
            MessageEnvelopeProperties messageEnvelopeProperties,
            FileStorageProperties fileStorageProperties,
            TraceDumpProperties traceDumpProperties,
            OomInspectorProperties oomInspectorProperties,
            Environment environment
    ) {
        this.messageEnvelopeFileService = messageEnvelopeFileService;
        this.messageEnvelopeProperties = messageEnvelopeProperties;
        this.fileStorageProperties = fileStorageProperties;
        this.traceDumpProperties = traceDumpProperties;
        this.oomInspectorProperties = oomInspectorProperties;
        this.environment = environment;
    }

    public Map<String, Object> buildInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("applicationName", applicationName);
        info.put("activeProfiles", List.of(environment.getActiveProfiles()));
        info.put("apId", apId);
        info.put("serverPort", serverPort);
        info.put("loggingPath", Path.of(loggingPath).toAbsolutePath().normalize().toString());

        info.put("messageLog", messageEnvelopeFileService.storageInfo());
        info.put("fileStorage", fileStorageSection());
        info.put("traceDump", traceDumpSection());
        info.put("oomInspector", oomInspectorSection());
        info.put("integrations", integrationSection());
        info.put("actuatorEndpoints", actuatorEndpoints());
        info.put("consoleLinks", consoleLinks());
        return info;
    }

    private Map<String, Object> fileStorageSection() {
        Path base = Path.of(fileStorageProperties.getStoragePath()).toAbsolutePath().normalize();
        Map<String, Object> section = new LinkedHashMap<>();
        section.put("storagePath", base.toString());
        section.put("pathPattern", base + "/yyyy/MM/dd/{storedName}");
        section.put("maxFileSizeBytes", fileStorageProperties.getMaxFileSizeBytes());
        section.put("allowedExtensions", fileStorageProperties.getAllowedExtensions());
        section.put("directoryExists", Files.isDirectory(base));
        return section;
    }

    private Map<String, String> traceDumpSection() {
        Path base = Path.of(traceDumpProperties.getEvidencePath()).toAbsolutePath().normalize();
        Map<String, String> section = new LinkedHashMap<>();
        section.put("evidencePath", base.toString());
        section.put("apiPath", "/api/v1/trace-dump");
        section.put("uiPath", "/tracedump");
        section.put("directoryExists", String.valueOf(Files.isDirectory(base)));
        return section;
    }

    private Map<String, String> oomInspectorSection() {
        Map<String, String> section = new LinkedHashMap<>();
        section.put("defaultSourceRoot", Path.of(oomInspectorProperties.getDefaultSourceRoot())
                .toAbsolutePath().normalize().toString());
        section.put("defaultMapperRoot", Path.of(oomInspectorProperties.getDefaultMapperRoot())
                .toAbsolutePath().normalize().toString());
        section.put("defaultConfigPath", Path.of(oomInspectorProperties.getDefaultConfigPath())
                .toAbsolutePath().normalize().toString());
        section.put("profileName", oomInspectorProperties.getProfileName());
        section.put("apiPath", "/api/oom-inspector");
        section.put("uiPath", "/oominspector");
        return section;
    }

    private Map<String, String> integrationSection() {
        Map<String, String> section = new LinkedHashMap<>();
        section.put("cruzApimBaseUrl", cruzApimBaseUrl);
        section.put("messageLogEnabled", String.valueOf(messageEnvelopeProperties.isEnabled()));
        section.put("configuredMessageLogPath", messageEnvelopeProperties.getStoragePath());
        section.put("configuredFileStoragePath", fileStorageProperties.getStoragePath());
        return section;
    }

    private List<Map<String, String>> actuatorEndpoints() {
        List<Map<String, String>> endpoints = new ArrayList<>();
        endpoints.add(endpoint("Health Check", "/actuator/health", "GET", "애플리케이션·DB 등 상태"));
        endpoints.add(endpoint("Application Info", "/actuator/info", "GET", "빌드·버전 정보"));
        endpoints.add(endpoint("Metrics Index", "/actuator/metrics", "GET", "지표 이름 목록"));
        endpoints.add(endpoint("Prometheus", "/actuator/prometheus", "GET", "Prometheus 텍스트 형식"));
        return endpoints;
    }

    private Map<String, String> endpoint(String name, String path, String method, String description) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("name", name);
        row.put("path", path);
        row.put("method", method);
        row.put("description", description);
        return row;
    }

    private List<Map<String, String>> consoleLinks() {
        List<Map<String, String>> links = new ArrayList<>();
        links.add(link("H2 Console", "/h2-console", h2ConsoleEnabled()));
        links.add(link("메시지 API", "/api/v1/messages", "always"));
        links.add(link("파일 API", "/api/v1/files", "always"));
        links.add(link("트랜잭션 로그 API", "/api/v1/transaction-logs", "always"));
        return links;
    }

    private Map<String, String> link(String name, String path, String note) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("name", name);
        row.put("path", path);
        row.put("note", note);
        return row;
    }

    private String h2ConsoleEnabled() {
        return environment.getProperty("spring.h2.console.enabled", Boolean.class, false)
                ? "enabled (local)"
                : "disabled";
    }
}
