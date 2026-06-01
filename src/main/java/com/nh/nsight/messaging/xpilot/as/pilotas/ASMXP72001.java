package com.nh.nsight.messaging.xpilot.as.pilotas;

import com.nh.nsight.messaging.xpilot.dc.pilotdc.DCPilot;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * traceenvironment 읽기 전용 — Pilot 구조 전환 검증용.
 */
@Service
public class ASMXP72001 {

    private final DCPilot dcPilot;

    public ASMXP72001(DCPilot dcPilot) {
        this.dcPilot = dcPilot;
    }

    public Map<String, Object> dashboardSummary(String runId) {
        return dcPilot.loadEnvironmentDashboardSummary(runId);
    }

    public Map<String, Object> settingsSummary() {
        return dcPilot.loadEnvironmentSettingsSummary();
    }
}
