package com.nh.nsight.messaging.tracedump.parser;

import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.EvidenceType;
import com.nh.nsight.messaging.tracedump.model.HsErrSnapshot;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HsErrParser {

    private static final Pattern SIGNAL = Pattern.compile("#\\s*SIG[A-Z]+\\s*\\((.*?)\\)");
    private static final Pattern FRAME = Pattern.compile("#\\s*V\\s+\\[.+\\]\\s+(.+)");

    public List<HsErrSnapshot> parse(List<EvidenceFile> files) {
        return files.stream()
                .filter(f -> f.type() == EvidenceType.HS_ERR)
                .map(this::parseOne)
                .toList();
    }

    public Optional<String> resolveOomCategory(List<EvidenceFile> files) {
        String combined = files.stream()
                .map(EvidenceFile::content)
                .reduce("", (a, b) -> a + "\n" + b)
                .toLowerCase(Locale.ROOT);

        if (combined.contains("java heap space") || combined.contains("gc overhead limit exceeded")) {
            return Optional.of("HEAP_OOM");
        }
        if (combined.contains("direct buffer memory")) {
            return Optional.of("DIRECT_BUFFER_OOM");
        }
        if (combined.contains("metaspace") || combined.contains("class metaspace")) {
            return Optional.of("METASPACE_OOM");
        }
        if (combined.contains("unable to create new native thread")) {
            return Optional.of("NATIVE_THREAD_OOM");
        }
        if (combined.contains("killed process") || combined.contains("out of memory")) {
            return Optional.of("OS_OOM_KILLER");
        }
        if (combined.contains("sigsegv") || combined.contains("fatal error")) {
            return Optional.of("JVM_CRASH");
        }
        return Optional.empty();
    }

    private HsErrSnapshot parseOne(EvidenceFile file) {
        String content = file.content();
        String category = resolveOomCategory(List.of(file)).orElse("UNKNOWN");
        String signal = "";
        Matcher signalMatcher = SIGNAL.matcher(content);
        if (signalMatcher.find()) {
            signal = signalMatcher.group(0);
        }
        String frame = "";
        Matcher frameMatcher = FRAME.matcher(content);
        if (frameMatcher.find()) {
            frame = frameMatcher.group(1);
        }
        String currentThread = extractLineValue(content, "Current thread");
        return new HsErrSnapshot(file.fileName(), category, signal, frame, currentThread);
    }

    private String extractLineValue(String content, String key) {
        for (String line : content.split("\n")) {
            if (line.contains(key)) {
                return line.trim();
            }
        }
        return "";
    }
}
