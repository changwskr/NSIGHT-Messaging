package com.nh.nsight.messaging.tracedump.model;

import java.util.List;

public record NmtSnapshot(
        String sourceFile,
        long totalCommittedKb,
        List<NmtLine> topLines
) {
    public record NmtLine(String category, long committedKb, long reservedKb) {
    }
}
