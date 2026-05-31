package com.nh.nsight.messaging.tracedump.model;

public record GcLogSnapshot(
        String sourceFile,
        int pauseEventCount,
        long maxPauseMs,
        int fullGcCount,
        int humongousMentionCount,
        int evacuationFailureCount,
        int mixedGcCount,
        int oldRegionUnchangedCount,
        int heapUnchangedAfterFullGcCount,
        int softReferenceCompactionCount
) {
}
