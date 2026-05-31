package com.nh.nsight.messaging.tracedump.model;

public record DumpAnalysisIndicators(
        boolean javaHeapOom,
        String oomCategory,
        int fullGcCount,
        boolean oldRegionNotReduced,
        boolean heapNotReducedAfterFullGc,
        boolean deadlockFound,
        int hikariWaitingThreads,
        int cruzApimWaitingThreads,
        int blockedThreads,
        int nmtThreadCount,
        boolean sessionCacheHint,
        boolean queryResultCacheHint,
        boolean heapDumpCollected
) {
}
