package com.nh.nsight.messaging.traceenvironment.model;

public record EnvSettingItemView(
        String key,
        String label,
        String guideValue,
        String actualValue,
        String source,
        String layer,
        SettingMatchStatus status,
        String note
) {
}
