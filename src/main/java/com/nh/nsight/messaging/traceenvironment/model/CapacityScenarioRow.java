package com.nh.nsight.messaging.traceenvironment.model;

public record CapacityScenarioRow(
        int percent,
        int actualRequestUsers,
        int peakTps,
        long tpmcTotal,
        int coresRequiredMin,
        int coresRequiredMax,
        boolean withinVmMaxTps,
        int concurrentPerAp
) {
}
