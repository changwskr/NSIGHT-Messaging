package com.nh.nsight.messaging.xpcapacitymgr.as.capacityas;

import org.springframework.stereotype.Service;

import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCDtoConverter;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCalculationCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCalculationResultCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.dc.capacitydc.DCCapacity;
import com.nh.nsight.messaging.xpcapacitymgr.dc.capacitydc.dto.CapacityCalculationDDTO;
import com.nh.nsight.messaging.xpcapacitymgr.util.CapacityCalcStep;

@Service
public class ASMSC71001 {

    private static final String AS = "ASMSC71001";

    private final DCCapacity dcCapacity;

    public ASMSC71001(DCCapacity dcCapacity) {
        this.dcCapacity = dcCapacity;
    }

    public CapacityCalculationCDTO defaults() {
        System.out.println("★★★★★ [" + AS + "] defaults");
        return CapacityCDtoConverter.defaultRequest();
    }

    public CapacityCalculationResultCDTO calculate(CapacityCalculationCDTO request) {
        CapacityCalcStep step = CapacityCalcStep.resolve(request.getCalculationStep());
        System.out.println("★★★★★ [" + AS + "] calculate START step=" + step.getCode());
        CapacityCalculationDDTO domain = CapacityCDtoConverter.toDomain(request);
        CapacityCalculationResultCDTO result = dcCapacity.calculate(domain, step);
        System.out.println("★★★★★ [" + AS + "] calculate END step=" + step.getCode()
                + " scenarioId=" + result.getScenarioId());
        return result;
    }
}
