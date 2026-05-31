package com.nh.nsight.messaging.traceenvironment.model;

import java.util.List;

public record CapacityDesignView(
        String scenarioId,
        CapacityPlannerResult planner,
        List<StackLayerView> stackLayers,
        List<LayerGridRow> layerGrid,
        JvmSizingRecommendation jvmSizing,
        boolean stackValid,
        int activeResponseTimeoutSec,
        int activeSessionMinutes
) {
}
