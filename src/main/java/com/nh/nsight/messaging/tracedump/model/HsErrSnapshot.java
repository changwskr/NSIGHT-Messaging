package com.nh.nsight.messaging.tracedump.model;

public record HsErrSnapshot(
        String sourceFile,
        String problemCategory,
        String signalName,
        String problematicFrame,
        String currentThread
) {
}
