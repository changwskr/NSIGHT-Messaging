package com.nh.nsight.messaging.tracedump.collector;

import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.EvidenceType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Component
public class EvidenceLoader {

    public List<EvidenceFile> loadFromDirectory(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("증거 디렉터리가 존재하지 않습니다: " + directory);
        }
        List<EvidenceFile> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                    String name = path.getFileName().toString();
                    String content = Files.readString(path, StandardCharsets.UTF_8);
                    files.add(new EvidenceFile(classify(name), name, content));
                } catch (IOException ex) {
                    throw new IllegalStateException("파일 읽기 실패: " + path, ex);
                }
            });
        }
        return files;
    }

    private EvidenceType classify(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.contains("thread") || lower.endsWith(".tdump") || lower.contains("jstack")) {
            return EvidenceType.THREAD_DUMP;
        }
        if (lower.contains("hs_err") || lower.startsWith("hs_err")) {
            return EvidenceType.HS_ERR;
        }
        if (lower.contains("nmt") || lower.contains("native_memory")) {
            return EvidenceType.NMT;
        }
        if (lower.contains("histo") || lower.contains("histogram")) {
            return EvidenceType.CLASS_HISTOGRAM;
        }
        if (lower.contains("gc") || lower.contains("gclog")) {
            return EvidenceType.GC_LOG;
        }
        if (lower.contains("dmesg") || lower.contains("journal") || lower.contains("pmap")) {
            return EvidenceType.OS_LOG;
        }
        return EvidenceType.UNKNOWN;
    }
}
