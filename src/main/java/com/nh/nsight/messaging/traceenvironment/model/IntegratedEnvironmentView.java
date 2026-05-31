package com.nh.nsight.messaging.traceenvironment.model;

import java.util.List;
import java.util.Map;

public record IntegratedEnvironmentView(
        String guideTitle,
        String guideVersion,
        String applicationName,
        List<String> activeProfiles,
        String apId,
        Map<String, String> baselineSummary,
        List<String> designRules,
        List<String> configurationCriteria,
        List<EnvSettingCategoryView> categories,
        int matchCount,
        int warnCount,
        int totalCompared
) {
}
