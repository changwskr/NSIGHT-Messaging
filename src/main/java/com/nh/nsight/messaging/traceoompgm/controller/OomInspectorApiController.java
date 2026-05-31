package com.nh.nsight.messaging.traceoompgm.controller;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.traceoompgm.config.OomInspectorProperties;
import com.nh.nsight.messaging.traceoompgm.model.OomFileContentView;
import com.nh.nsight.messaging.traceoompgm.model.OomScanReport;
import com.nh.nsight.messaging.traceoompgm.service.OomInspectorFileService;
import com.nh.nsight.messaging.traceoompgm.service.OomInspectorScanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/oom-inspector")
public class OomInspectorApiController {

    private final OomInspectorScanService scanService;
    private final OomInspectorFileService fileService;
    private final OomInspectorProperties properties;

    public OomInspectorApiController(
            OomInspectorScanService scanService,
            OomInspectorFileService fileService,
            OomInspectorProperties properties
    ) {
        this.scanService = scanService;
        this.fileService = fileService;
        this.properties = properties;
    }

    @PostMapping("/scans")
    public StandardResponse<OomScanReport> startScan(
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String sourceRoot,
            @RequestParam(required = false) String mapperRoot,
            @RequestParam(required = false) String configPath,
            @RequestParam(defaultValue = "true") boolean failOnCritical
    ) throws Exception {
        OomScanReport report = scanService.scan(projectName, sourceRoot, mapperRoot, configPath, failOnCritical);
        return StandardResponse.success("OOM-SCAN-001", "oomInspectorScan", report);
    }

    @PostMapping("/scans/quick")
    public StandardResponse<OomScanReport> quickScan() throws Exception {
        OomScanReport report = scanService.scanDefaultProject();
        return StandardResponse.success("OOM-SCAN-QUICK-001", "oomInspectorQuickScan", report);
    }

    @PostMapping("/gate")
    public StandardResponse<Map<String, Object>> qualityGate(
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String sourceRoot,
            @RequestParam(required = false) String mapperRoot,
            @RequestParam(required = false) String configPath,
            @RequestParam(defaultValue = "CRITICAL") String failOn
    ) throws Exception {
        OomScanReport report = scanService.scan(projectName, sourceRoot, mapperRoot, configPath, true);
        boolean passed = scanService.evaluateGate(report, failOn);
        return StandardResponse.success("OOM-GATE-001", "oomInspectorGate", Map.of(
                "passed", passed,
                "failOn", failOn,
                "gateMessage", report.gateMessage(),
                "findingsBySeverity", report.findingsBySeverity(),
                "scanId", report.scanId()
        ));
    }

    @GetMapping("/files/content")
    public StandardResponse<OomFileContentView> fileContent(
            @RequestParam String relativePath,
            @RequestParam(required = false) String sourceRoot,
            @RequestParam(required = false) String mapperRoot,
            @RequestParam(required = false) String configPath,
            @RequestParam(defaultValue = "0") int line
    ) throws Exception {
        OomFileContentView view = fileService.readContent(relativePath, sourceRoot, mapperRoot, configPath, line);
        return StandardResponse.success("OOM-FILE-001", "oomInspectorFileContent", view);
    }

    @GetMapping("/defaults")
    public StandardResponse<Map<String, String>> defaults() {
        return StandardResponse.success("OOM-DEF-001", "oomInspectorDefaults", Map.of(
                "defaultSourceRoot", properties.getDefaultSourceRoot(),
                "defaultMapperRoot", properties.getDefaultMapperRoot(),
                "defaultConfigPath", properties.getDefaultConfigPath(),
                "profileName", properties.getProfileName()
        ));
    }
}
