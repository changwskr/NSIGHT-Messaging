package com.nh.nsight.messaging.tracedump.model;

public record OomCorrelation(
        String id,
        String problemArea,
        String logEvidence,
        String relatedProgram,
        String probableCause,
        String evidenceFile,
        Severity severity
) {
}
