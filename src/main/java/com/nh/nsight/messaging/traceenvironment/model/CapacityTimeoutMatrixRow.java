package com.nh.nsight.messaging.traceenvironment.model;

public record CapacityTimeoutMatrixRow(
        int percent,
        int actualRequestUsers,
        int responseTimeoutSec,
        int peakTps,
        long tpmcTotal
) {
}
