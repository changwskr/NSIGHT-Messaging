package com.nh.nsight.messaging.tracedump.parser;

import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.ThreadDumpSnapshot;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class ThreadDumpParser {

    private static final Pattern THREAD_LINE = Pattern.compile("^\"(.+)\".*$");
    private static final Pattern STATE_LINE = Pattern.compile("\\s+java\\.lang\\.Thread\\.State:\\s+(\\w+)");

    public List<ThreadDumpSnapshot> parse(List<EvidenceFile> files) {
        return files.stream()
                .filter(f -> f.type() == com.nh.nsight.messaging.tracedump.model.EvidenceType.THREAD_DUMP)
                .map(this::parseOne)
                .toList();
    }

    private ThreadDumpSnapshot parseOne(EvidenceFile file) {
        String content = file.content();
        String[] lines = content.split("\n");
        Map<String, Integer> stateCounts = new HashMap<>();
        int blocked = 0;
        int waiting = 0;
        int timedWaiting = 0;
        int runnable = 0;
        int hikari = 0;
        int jdbc = 0;
        int http = 0;
        int cruzApim = 0;
        boolean deadlock = content.contains("Found one Java-level deadlock")
                || content.contains("Found 1 deadlock");

        String currentThreadBlock = "";
        int threadCount = 0;
        for (String raw : lines) {
            String line = raw.trim();
            if (line.startsWith("\"") && line.contains(" #")) {
                threadCount++;
                currentThreadBlock = line.toLowerCase(Locale.ROOT);
            }
            var stateMatcher = STATE_LINE.matcher(raw);
            if (stateMatcher.find()) {
                String state = stateMatcher.group(1);
                stateCounts.merge(state, 1, Integer::sum);
                switch (state) {
                    case "BLOCKED" -> blocked++;
                    case "WAITING" -> waiting++;
                    case "TIMED_WAITING" -> timedWaiting++;
                    case "RUNNABLE" -> runnable++;
                    default -> { }
                }
            }
            String lowerBlock = (currentThreadBlock + " " + line).toLowerCase(Locale.ROOT);
            if (lowerBlock.contains("hikaripool.getconnection") || lowerBlock.contains("hikari")) {
                hikari++;
            }
            if (lowerBlock.contains("oracle.jdbc") || lowerBlock.contains("executequery")
                    || lowerBlock.contains("preparedstatement")) {
                jdbc++;
            }
            if (lowerBlock.contains("httpclient") || lowerBlock.contains("resttemplate")
                    || lowerBlock.contains("webclient")) {
                http++;
            }
            if (lowerBlock.contains("cruzapim") || lowerBlock.contains("cruz api")) {
                cruzApim++;
            }
        }

        return new ThreadDumpSnapshot(
                file.fileName(),
                threadCount,
                stateCounts,
                blocked,
                waiting,
                timedWaiting,
                runnable,
                deadlock,
                hikari,
                jdbc,
                http,
                cruzApim
        );
    }
}
