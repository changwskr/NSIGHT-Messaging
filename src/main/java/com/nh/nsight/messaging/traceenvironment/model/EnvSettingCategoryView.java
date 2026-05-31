package com.nh.nsight.messaging.traceenvironment.model;

import java.util.List;

public record EnvSettingCategoryView(
        String id,
        String title,
        String description,
        List<EnvSettingItemView> items
) {
}
