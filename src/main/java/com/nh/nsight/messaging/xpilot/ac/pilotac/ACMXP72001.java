package com.nh.nsight.messaging.xpilot.ac.pilotac;

import com.nh.nsight.messaging.xpilot.ac.pilotac.dto.PilotApiResponse;
import com.nh.nsight.messaging.xpilot.as.pilotas.ASMXP72001;
import com.nh.nsight.messaging.xpilot.zcommonutil.XpilotBizException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/xpilot/environment")
public class ACMXP72001 {

    private static final String AC = "ACMXP72001";

    private final ASMXP72001 asmxp72001;

    public ACMXP72001(ASMXP72001 asmxp72001) {
        this.asmxp72001 = asmxp72001;
    }

    @GetMapping("/dashboard-summary")
    public ResponseEntity<PilotApiResponse<Map<String, Object>>> dashboardSummary(
            @RequestParam(required = false) String runId
    ) {
        System.out.println("[" + AC + "] dashboardSummary START runId=" + runId);
        ResponseEntity<PilotApiResponse<Map<String, Object>>> response =
                ResponseEntity.ok(PilotApiResponse.ok(asmxp72001.dashboardSummary(runId)));
        System.out.println("[" + AC + "] dashboardSummary END runId=" + runId);
        return response;
    }

    @GetMapping("/settings-summary")
    public ResponseEntity<PilotApiResponse<Map<String, Object>>> settingsSummary() {
        System.out.println("[" + AC + "] settingsSummary START");
        ResponseEntity<PilotApiResponse<Map<String, Object>>> response =
                ResponseEntity.ok(PilotApiResponse.ok(asmxp72001.settingsSummary()));
        System.out.println("[" + AC + "] settingsSummary END");
        return response;
    }

    @ExceptionHandler(XpilotBizException.class)
    public ResponseEntity<PilotApiResponse<Void>> handleBiz(XpilotBizException ex) {
        System.out.println("[" + AC + "] handleBiz START message=" + ex.getMessage());
        ResponseEntity<PilotApiResponse<Void>> response =
                ResponseEntity.badRequest().body(PilotApiResponse.fail(ex.getMessage()));
        System.out.println("[" + AC + "] handleBiz END");
        return response;
    }
}
