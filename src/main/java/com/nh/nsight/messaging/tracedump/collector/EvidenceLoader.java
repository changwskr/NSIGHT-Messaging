package com.nh.nsight.messaging.tracedump.collector;

import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.EvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class EvidenceLoader {

    private static final Logger log = LoggerFactory.getLogger(EvidenceLoader.class);

    private static final Set<String> BINARY_EXTENSIONS = Set.of(
            ".hprof", ".phd", ".bin", ".exe", ".dll", ".so", ".dylib", ".class", ".jar", ".zip",
            ".gz", ".bz2", ".xz", ".7z", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".pdf", ".woff", ".woff2"
    );

    public List<EvidenceFile> loadFromDirectory(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("증거 디렉터리가 존재하지 않습니다: " + directory);
        }
        List<EvidenceFile> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                String name = path.getFileName().toString();
                if (isBinaryExtension(name)) {
                    registerBinaryEvidence(files, name);
                    log.debug("Skipped binary evidence file: {}", path);
                    return;
                }
                try {
                    String content = Files.readString(path, StandardCharsets.UTF_8);
                    files.add(new EvidenceFile(classify(name), name, content));
                } catch (IOException ex) {
                    if (isNonTextEncodingFailure(ex)) {
                        registerBinaryEvidence(files, name);
                        log.debug("Skipped non-UTF-8 evidence file: {}", path);
                        return;
                    }
                    throw new IllegalStateException("파일 읽기 실패: " + path, ex);
                }
            });
        }
        return files;
    }

    private void registerBinaryEvidence(List<EvidenceFile> files, String fileName) {
        EvidenceType type = classify(fileName);
        if (type == EvidenceType.UNKNOWN && isHeapDumpFile(fileName)) {
            type = EvidenceType.HEAP_DUMP;
        }
        if (type == EvidenceType.HEAP_DUMP) {
            files.add(new EvidenceFile(type, fileName, ""));
        }
    }

    private boolean isBinaryExtension(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        return BINARY_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    private boolean isHeapDumpFile(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        return lower.endsWith(".hprof") || lower.endsWith(".phd");
    }

    private boolean isNonTextEncodingFailure(IOException ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof MalformedInputException) {
                return true;
            }
            cause = cause.getCause();
        }
        return ex instanceof CharacterCodingException;
    }

    private EvidenceType classify(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".hprof") || lower.endsWith(".phd")) {
            return EvidenceType.HEAP_DUMP;
        }
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
        if (lower.contains("console") && (lower.contains("heap") || lower.contains("oom"))) {
            return EvidenceType.HEAP_CONSOLE;
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
