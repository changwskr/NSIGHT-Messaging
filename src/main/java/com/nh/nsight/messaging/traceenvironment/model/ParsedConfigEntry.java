package com.nh.nsight.messaging.traceenvironment.model;

public record ParsedConfigEntry(
        String fileName,
        String configKey,
        String configValue,
        String normalizedKey,
        int sourceLine
) {
}
