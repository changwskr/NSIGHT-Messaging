package com.nh.nsight.messaging.tracedump.parser;

import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.EvidenceType;
import com.nh.nsight.messaging.tracedump.model.GcLogSnapshot;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GcLogParser {

    private static final Pattern PAUSE_MS = Pattern.compile("Pause.*?(\\d+\\.?\\d*)\\s*ms", Pattern.CASE_INSENSITIVE);
    private static final Pattern OLD_REGIONS = Pattern.compile("Old regions:\\s*(\\d+)\\s*->\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern HEAP_SAME = Pattern.compile("(\\d+(?:\\.\\d+)?)([KMG])\\s*->\\s*\\1\\2", Pattern.CASE_INSENSITIVE);

    public List<GcLogSnapshot> parse(List<EvidenceFile> files) {
        return files.stream()
                .filter(f -> f.type() == EvidenceType.GC_LOG || looksLikeGcLog(f))
                .map(this::parseOne)
                .toList();
    }

    private boolean looksLikeGcLog(EvidenceFile file) {
        String lower = file.content().toLowerCase(Locale.ROOT);
        return lower.contains("[gc") || lower.contains("pause young") || lower.contains("g1");
    }

    private GcLogSnapshot parseOne(EvidenceFile file) {
        String content = file.content();
        String lower = content.toLowerCase(Locale.ROOT);
        int pauseCount = 0;
        long maxPause = 0;
        Matcher matcher = PAUSE_MS.matcher(content);
        while (matcher.find()) {
            pauseCount++;
            try {
                long ms = Math.round(Double.parseDouble(matcher.group(1)));
                maxPause = Math.max(maxPause, ms);
            } catch (NumberFormatException ignored) {
            }
        }
        int fullGc = count(lower, "full gc") + count(lower, "pause full");
        int humongous = count(lower, "humongous");
        int evacFail = count(lower, "to-space exhausted") + count(lower, "evacuation failure");
        int mixed = count(lower, "mixed gc");
        int oldUnchanged = 0;
        Matcher oldMatcher = OLD_REGIONS.matcher(content);
        while (oldMatcher.find()) {
            if (oldMatcher.group(1).equals(oldMatcher.group(2))) {
                oldUnchanged++;
            }
        }
        int heapUnchanged = 0;
        Matcher heapMatcher = HEAP_SAME.matcher(content);
        while (heapMatcher.find()) {
            heapUnchanged++;
        }
        int softRef = count(lower, "clearing soft references");
        return new GcLogSnapshot(
                file.fileName(), pauseCount, maxPause, fullGc, humongous, evacFail, mixed,
                oldUnchanged, heapUnchanged, softRef
        );
    }

    private int count(String text, String token) {
        int idx = 0;
        int count = 0;
        while ((idx = text.indexOf(token, idx)) >= 0) {
            count++;
            idx += token.length();
        }
        return count;
    }
}
