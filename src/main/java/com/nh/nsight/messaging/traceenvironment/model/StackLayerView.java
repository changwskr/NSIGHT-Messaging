package com.nh.nsight.messaging.traceenvironment.model;

import java.util.List;

public record StackLayerView(
        int order,
        String layerId,
        String layerName,
        String description,
        boolean layerValid,
        List<StackSettingRow> settings
) {
}
