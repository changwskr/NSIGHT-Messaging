package com.nh.nsight.messaging.xpilot.dc.pilotdc;

import com.nh.nsight.messaging.xpilot.dc.pilotdc.dto.PilotDDTO;

import java.util.List;
import java.util.Map;

public interface IDCPilot {

    PilotDDTO getPilot(PilotDDTO criteria);

    void createPilot(PilotDDTO pilotDDTO);

    List<PilotDDTO> listPilots(PilotDDTO criteria);

    Map<String, Object> loadEnvironmentDashboardSummary(String runId);

    Map<String, Object> loadEnvironmentSettingsSummary();
}
