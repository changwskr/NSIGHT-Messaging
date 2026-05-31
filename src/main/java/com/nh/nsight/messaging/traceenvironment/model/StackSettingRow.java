package com.nh.nsight.messaging.traceenvironment.model;

public record StackSettingRow(
        String settingLabel,
        String propertyKey,
        String configFile,
        String actualValue,
        String recommendedValue,
        String status,
        String statusLabel,
        String reason,
        String settingExample,
        String actionGuide,
        String note
) {
}
