package com.nh.nsight.messaging.tracedump.controller;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.config.TraceDumpProperties;
import com.nh.nsight.messaging.tracedump.dto.TraceDumpAnalysisResponse;
import com.nh.nsight.messaging.tracedump.service.TraceDumpAnalysisService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trace-dump")
public class TraceDumpController {

    private final TraceDumpAnalysisService traceDumpAnalysisService;
    private final TraceDumpProperties traceDumpProperties;

    public TraceDumpController(
            TraceDumpAnalysisService traceDumpAnalysisService,
            TraceDumpProperties traceDumpProperties
    ) {
        this.traceDumpAnalysisService = traceDumpAnalysisService;
        this.traceDumpProperties = traceDumpProperties;
    }

    @PostMapping("/analyze")
    public StandardResponse<TraceDumpAnalysisResponse> analyze(
            @RequestParam(required = false) String evidencePath,
            @RequestParam(required = false) MultipartFile evidenceZip
    ) throws Exception {
        TraceDumpAnalysisResponse response;
        if (evidenceZip != null && !evidenceZip.isEmpty()) {
            Path base = Path.of(traceDumpProperties.getEvidencePath());
            response = TraceDumpAnalysisResponse.from(
                    traceDumpAnalysisService.analyzeUploadedZip(evidenceZip, base)
            );
        } else if (StringUtils.hasText(evidencePath)) {
            response = TraceDumpAnalysisResponse.from(
                    traceDumpAnalysisService.analyzeDirectory(Path.of(evidencePath))
            );
        } else {
            throw new IllegalArgumentException("evidencePath 또는 evidenceZip 중 하나는 필수입니다.");
        }
        return StandardResponse.success("TRACE-DUMP-001", "traceDumpAnalyze", response);
    }

    @GetMapping("/storage-location")
    public StandardResponse<Map<String, String>> storageLocation() {
        Path base = Path.of(traceDumpProperties.getEvidencePath()).toAbsolutePath().normalize();
        return StandardResponse.success("TRACE-DUMP-LOC-001", "traceDumpStorageLocation", Map.of(
                "storagePath", base.toString(),
                "acceptedFiles", "thread*.tdump, gc*.log, hs_err*, nmt*.txt, histo*.txt, dmesg*.log (ZIP 업로드 가능)"
        ));
    }

    @GetMapping("/sample-dirs")
    public StandardResponse<List<String>> sampleDirs() throws Exception {
        List<String> dirs = traceDumpAnalysisService.listSampleEvidenceDirs(
                Path.of(traceDumpProperties.getEvidencePath())
        );
        return StandardResponse.success("TRACE-DUMP-LIST-001", "traceDumpSampleDirs", dirs);
    }
}
