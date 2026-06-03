package com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityApiResponse;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCalculationCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCalculationResultCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.as.capacityas.ASMSC71001;
import com.nh.nsight.messaging.xpcapacitymgr.util.XpCapacityBizException;

@RestController
@RequestMapping("/api/xpcapacitymgr")
public class ACMSC71001 {

    private static final String AC = "ACMSC71001";

    private final ASMSC71001 asmsc71001;

    public ACMSC71001(ASMSC71001 asmsc71001) {
        this.asmsc71001 = asmsc71001;
    }

    @GetMapping("/defaults")
    public ResponseEntity<CapacityApiResponse<CapacityCalculationCDTO>> defaults() {
        System.out.println("★★★★★ [" + AC + "] defaults START/END");
        return ResponseEntity.ok(CapacityApiResponse.ok(asmsc71001.defaults(), "기본 산정 조건"));
    }

    @PostMapping("/calculate")
    public ResponseEntity<CapacityApiResponse<CapacityCalculationResultCDTO>> calculate(
            @RequestBody CapacityCalculationCDTO request) {
        System.out.println("★★★★★ [" + AC + "] calculate START");
        request.setCalculationStep(null);
        CapacityCalculationResultCDTO result = asmsc71001.calculate(request);
        System.out.println("★★★★★ [" + AC + "] calculate END scenarioId=" + result.getScenarioId());
        return ResponseEntity.ok(CapacityApiResponse.ok(result, "전체 용량 산정이 완료되었습니다."));
    }

    @PostMapping("/calculate-step")
    public ResponseEntity<CapacityApiResponse<CapacityCalculationResultCDTO>> calculateStep(
            @RequestBody CapacityCalculationCDTO request) {
        if (request.getCalculationStep() == null || request.getCalculationStep().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(CapacityApiResponse.fail("calculationStep가 필요합니다 (020, 030, 040, 050)"));
        }
        System.out.println("★★★★★ [" + AC + "] calculateStep START step=" + request.getCalculationStep());
        CapacityCalculationResultCDTO result = asmsc71001.calculate(request);
        String label = result.getCalculatedStepLabel() != null ? result.getCalculatedStepLabel() : "단계";
        System.out.println("★★★★★ [" + AC + "] calculateStep END " + label);
        return ResponseEntity.ok(CapacityApiResponse.ok(result, label + " 산정이 완료되었습니다."));
    }

    @ExceptionHandler(XpCapacityBizException.class)
    public ResponseEntity<CapacityApiResponse<Void>> handleBiz(XpCapacityBizException ex) {
        System.out.println("★★★★★ [" + AC + "] handleBiz " + ex.getMessage());
        return ResponseEntity.badRequest().body(CapacityApiResponse.fail(ex.getMessage()));
    }
}
