package com.nh.nsight.messaging.xpcapacitymgr.as.capacityas;

import com.nh.nsight.messaging.traceenvironment.guide.VmProfile;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCDtoConverter;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.WasThreadOnlyCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.WasThreadResultCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.dc.capacitydc.DCCapacity;
import com.nh.nsight.messaging.xpcapacitymgr.util.XpCapacityBizException;

import org.springframework.stereotype.Service;

@Service
public class ASMSC72001 {

    private static final String AS = "ASMSC72001";

    private final DCCapacity dcCapacity;

    public ASMSC72001(DCCapacity dcCapacity) {
        this.dcCapacity = dcCapacity;
    }

    public WasThreadResultCDTO calculateWasThread(WasThreadOnlyCDTO request) {
        System.out.println("★★★★★ [" + AS + "] calculateWasThread START tps="
                + (request != null ? request.getTargetTps() : 0));
        if (request == null || request.getTargetTps() <= 0) {
            throw new XpCapacityBizException("목표 TPS는 1 이상이어야 합니다.");
        }
        VmProfile profile = CapacityCDtoConverter.resolveVmProfile(request.getVmSpecCode());
        WasThreadResultCDTO result = dcCapacity.calculateWasThreadOnly(
                request.getTargetTps(),
                Math.max(1, request.getApCount()),
                request.getAvgThreadHoldSec(),
                request.getThreadMarginRate(),
                request.getMaxThreadMarginRate(),
                profile);
        System.out.println("★★★★★ [" + AS + "] calculateWasThread END status=" + result.getStatus());
        return result;
    }
}
