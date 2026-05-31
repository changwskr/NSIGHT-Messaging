package com.nh.nsight.messaging.tracedump.model;

import java.util.List;

public record ClassHistogramSnapshot(
        String sourceFile,
        List<HistogramEntry> topEntries
) {
    public record HistogramEntry(String className, long instances, long bytes) {
    }
}
