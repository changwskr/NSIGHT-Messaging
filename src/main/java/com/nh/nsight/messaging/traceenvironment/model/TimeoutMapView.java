package com.nh.nsight.messaging.traceenvironment.model;

import java.util.List;

public record TimeoutMapView(
        String runId,
        String chainRuleId,
        boolean chainValid,
        String chainSummary,
        List<TimeoutMapNode> nodes
) {
}
