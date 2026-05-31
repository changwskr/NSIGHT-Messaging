package com.nh.nsight.messaging.tracedump.service;

import com.nh.nsight.messaging.tracedump.analyzer.TraceDumpRuleEngine;
import com.nh.nsight.messaging.tracedump.collector.EvidenceLoader;
import com.nh.nsight.messaging.tracedump.model.AnalysisFinding;
import com.nh.nsight.messaging.tracedump.model.ClassHistogramSnapshot;
import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.GcLogSnapshot;
import com.nh.nsight.messaging.tracedump.model.HsErrSnapshot;
import com.nh.nsight.messaging.tracedump.model.NmtSnapshot;
import com.nh.nsight.messaging.tracedump.model.ThreadDumpSnapshot;
import com.nh.nsight.messaging.tracedump.model.TraceDumpAnalysisReport;
import com.nh.nsight.messaging.tracedump.parser.ClassHistogramParser;
import com.nh.nsight.messaging.tracedump.parser.GcLogParser;
import com.nh.nsight.messaging.tracedump.parser.HsErrParser;
import com.nh.nsight.messaging.tracedump.parser.NmtParser;
import com.nh.nsight.messaging.tracedump.parser.ThreadDumpParser;
import com.nh.nsight.messaging.tracedump.report.TraceDumpReportBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class TraceDumpAnalysisService {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final EvidenceLoader evidenceLoader;
    private final ThreadDumpParser threadDumpParser;
    private final GcLogParser gcLogParser;
    private final HsErrParser hsErrParser;
    private final NmtParser nmtParser;
    private final ClassHistogramParser classHistogramParser;
    private final TraceDumpRuleEngine ruleEngine;
    private final TraceDumpReportBuilder reportBuilder;

    public TraceDumpAnalysisService(
            EvidenceLoader evidenceLoader,
            ThreadDumpParser threadDumpParser,
            GcLogParser gcLogParser,
            HsErrParser hsErrParser,
            NmtParser nmtParser,
            ClassHistogramParser classHistogramParser,
            TraceDumpRuleEngine ruleEngine,
            TraceDumpReportBuilder reportBuilder
    ) {
        this.evidenceLoader = evidenceLoader;
        this.threadDumpParser = threadDumpParser;
        this.gcLogParser = gcLogParser;
        this.hsErrParser = hsErrParser;
        this.nmtParser = nmtParser;
        this.classHistogramParser = classHistogramParser;
        this.ruleEngine = ruleEngine;
        this.reportBuilder = reportBuilder;
    }

    public TraceDumpAnalysisReport analyzeDirectory(Path directory) throws IOException {
        List<EvidenceFile> files = evidenceLoader.loadFromDirectory(directory);
        return analyzeFiles(files, directory.toAbsolutePath().normalize().toString());
    }

    public TraceDumpAnalysisReport analyzeUploadedZip(MultipartFile zipFile, Path storageBase) throws IOException {
        Path workDir = storageBase.resolve("upload-" + STAMP.format(java.time.LocalDateTime.now()));
        Files.createDirectories(workDir);
        unzip(zipFile, workDir);
        return analyzeDirectory(workDir);
    }

    private TraceDumpAnalysisReport analyzeFiles(List<EvidenceFile> files, String evidencePath) {
        List<ThreadDumpSnapshot> threads = threadDumpParser.parse(files);
        List<GcLogSnapshot> gcLogs = gcLogParser.parse(files);
        List<HsErrSnapshot> hsErrs = hsErrParser.parse(files);
        List<NmtSnapshot> nmts = nmtParser.parse(files);
        List<ClassHistogramSnapshot> histograms = classHistogramParser.parse(files);

        String oomCategory = hsErrParser.resolveOomCategory(files).orElse("UNKNOWN");

        List<AnalysisFinding> findings = ruleEngine.evaluate(
                files, threads, gcLogs, hsErrs, nmts, histograms, oomCategory
        );

        return reportBuilder.build(evidencePath, oomCategory, findings, threads, gcLogs, files.size());
    }

    private void unzip(MultipartFile zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    Files.createDirectories(targetDir.resolve(entry.getName()));
                } else {
                    Path out = targetDir.resolve(entry.getName());
                    Files.createDirectories(out.getParent());
                    Files.write(out, zis.readAllBytes());
                }
                zis.closeEntry();
            }
        }
    }

    public List<String> listSampleEvidenceDirs(Path base) throws IOException {
        if (!Files.isDirectory(base)) {
            return List.of();
        }
        try (var stream = Files.list(base)) {
            return stream.filter(Files::isDirectory)
                    .map(p -> p.toAbsolutePath().normalize().toString())
                    .sorted(Comparator.reverseOrder())
                    .toList();
        }
    }
}
