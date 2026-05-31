package com.nh.nsight.messaging.tracedump.analyzer;

import com.nh.nsight.messaging.tracedump.model.DumpAnalysisIndicators;
import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.EvidenceType;
import com.nh.nsight.messaging.tracedump.model.GcLogSnapshot;
import com.nh.nsight.messaging.tracedump.model.NmtSnapshot;
import com.nh.nsight.messaging.tracedump.model.ThreadDumpSnapshot;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class DumpIndicatorsBuilder {

    public DumpAnalysisIndicators build(
            String oomCategory,
            List<EvidenceFile> files,
            List<ThreadDumpSnapshot> threads,
            List<GcLogSnapshot> gcLogs,
            List<NmtSnapshot> nmts
    ) {
        boolean heapOom = "HEAP_OOM".equals(oomCategory);
        int fullGc = gcLogs.stream().mapToInt(GcLogSnapshot::fullGcCount).sum();
        boolean oldStuck = gcLogs.stream().anyMatch(g -> g.oldRegionUnchangedCount() > 0);
        boolean heapStuck = gcLogs.stream().anyMatch(g -> g.heapUnchangedAfterFullGcCount() > 0);
        boolean deadlock = threads.stream().anyMatch(ThreadDumpSnapshot::deadlockDetected);
        int hikari = threads.stream().mapToInt(ThreadDumpSnapshot::hikariWaitCount).max().orElse(0);
        int cruz = threads.stream().mapToInt(ThreadDumpSnapshot::cruzApimWaitCount).max().orElse(0);
        int blocked = threads.stream().mapToInt(ThreadDumpSnapshot::blockedCount).max().orElse(0);
        int nmtThreads = nmts.stream()
                .flatMap(n -> n.topLines().stream())
                .filter(l -> "Thread".equalsIgnoreCase(l.category()))
                .mapToInt(l -> (int) (l.committedKb() / 1024))
                .max()
                .orElse(0);

        String combined = files.stream()
                .filter(f -> f.type() != EvidenceType.HEAP_DUMP)
                .map(EvidenceFile::content)
                .reduce("", (a, b) -> a + b)
                .toUpperCase(Locale.ROOT);

        boolean sessionCache = combined.contains("SESSION_CACHE") || combined.contains("NSIGHTSESSION");
        boolean queryCache = combined.contains("QUERY_RESULT_CACHE");
        boolean hprof = files.stream().anyMatch(f -> f.type() == EvidenceType.HEAP_DUMP);

        return new DumpAnalysisIndicators(
                heapOom,
                oomCategory,
                fullGc,
                oldStuck,
                heapStuck,
                deadlock,
                hikari,
                cruz,
                blocked,
                nmtThreads,
                sessionCache,
                queryCache,
                hprof
        );
    }
}
