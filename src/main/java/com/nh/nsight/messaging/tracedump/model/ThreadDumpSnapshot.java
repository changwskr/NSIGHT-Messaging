package com.nh.nsight.messaging.tracedump.model;

import java.util.Map;

public record ThreadDumpSnapshot(
        String sourceFile,
        int totalThreads,
        Map<String, Integer> stateCounts,
        int blockedCount,
        int waitingCount,
        int timedWaitingCount,
        int runnableCount,
        boolean deadlockDetected,
        int hikariWaitCount,
        int jdbcWaitCount,
        int httpClientWaitCount
) {
}
