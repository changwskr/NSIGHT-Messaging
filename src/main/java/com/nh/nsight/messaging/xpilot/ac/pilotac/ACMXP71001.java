package com.nh.nsight.messaging.xpilot.ac.pilotac;

import com.nh.nsight.messaging.xpilot.ac.pilotac.dto.PilotApiResponse;
import com.nh.nsight.messaging.xpilot.ac.pilotac.dto.PilotCDTO;
import com.nh.nsight.messaging.xpilot.as.pilotas.ASMXP71001;
import com.nh.nsight.messaging.xpilot.util.XpilotBizException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/xpilot/pilot")
public class ACMXP71001 {

    private static final String AC = "ACMXP71001";

    private final ASMXP71001 asmxp71001;

    public ACMXP71001(ASMXP71001 asmxp71001) {
        this.asmxp71001 = asmxp71001;
    }

    @PostMapping("/create")
    public ResponseEntity<PilotApiResponse<PilotCDTO>> create(@RequestBody PilotCDTO pilotCDTO) {
        System.out.println("▶▶▶▶▶▶▶▶▶▶[" + AC + "] create START");
        PilotCDTO created = asmxp71001.create(pilotCDTO);
        ResponseEntity<PilotApiResponse<PilotCDTO>> response = ResponseEntity
                .ok(PilotApiResponse.ok(created, "Pilot 세션이 생성되었습니다."));
        System.out.println("[" + AC + "] create END");
        return response;
    }

    @GetMapping("/{pilotId}")
    public ResponseEntity<PilotApiResponse<PilotCDTO>> get(@PathVariable String pilotId) {
        System.out.println("▶▶▶▶▶▶▶▶▶▶[" + AC + "] get START pilotId=" + pilotId);
        ResponseEntity<PilotApiResponse<PilotCDTO>> response = ResponseEntity
                .ok(PilotApiResponse.ok(asmxp71001.get(pilotId)));
        System.out.println("[" + AC + "] get END pilotId=" + pilotId);
        return response;
    }

    @GetMapping
    public ResponseEntity<PilotApiResponse<List<PilotCDTO>>> list() {
        System.out.println("▶▶▶▶▶▶▶▶▶▶[" + AC + "] list START");
        ResponseEntity<PilotApiResponse<List<PilotCDTO>>> response = ResponseEntity
                .ok(PilotApiResponse.okList(asmxp71001.list(null)));
        System.out.println("▶▶▶▶▶▶▶▶▶▶[" + AC + "] list END");
        return response;
    }

    @GetMapping("/create/sample")
    public ResponseEntity<PilotApiResponse<PilotCDTO>> createSample() {
        System.out.println("▶▶▶▶▶▶▶▶▶▶[" + AC + "] createSample START");
        PilotCDTO sample = new PilotCDTO();
        sample.setPilotName("traceenvironment 구조 전환 Pilot");
        sample.setNote("controller/service → ac/as/dc 검증");
        ResponseEntity<PilotApiResponse<PilotCDTO>> response = create(sample);
        System.out.println("[" + AC + "] createSample END");
        return response;
    }

    @ExceptionHandler(XpilotBizException.class)
    public ResponseEntity<PilotApiResponse<Void>> handleBiz(XpilotBizException ex) {
        System.out.println("▶▶▶▶▶▶▶▶▶▶[" + AC + "] handleBiz START message=" + ex.getMessage());
        ResponseEntity<PilotApiResponse<Void>> response = ResponseEntity.badRequest()
                .body(PilotApiResponse.fail(ex.getMessage()));
        System.out.println("[" + AC + "] handleBiz END");
        return response;
    }
}
