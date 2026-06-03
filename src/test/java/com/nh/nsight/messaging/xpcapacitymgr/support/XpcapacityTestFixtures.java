package com.nh.nsight.messaging.xpcapacitymgr.support;

import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCalculationCDTO;

import java.util.List;

public final class XpcapacityTestFixtures {

    private XpcapacityTestFixtures() {
    }

    /** 1,000명 · 5% · 3초 — 단일 시나리오 검증용 최소 입력. */
    public static CapacityCalculationCDTO minimalCalculationRequest() {
        CapacityCalculationCDTO dto = new CapacityCalculationCDTO();
        dto.setProjectName("테스트 시나리오");
        dto.setBranchCount(100);
        dto.setUserPerBranch(10);
        dto.setSessionMarginRate(0.10);
        dto.setSessionTimeoutMin(60);
        dto.setConcurrentRequestRates(List.of(0.05));
        dto.setTargetResponseTimes(List.of(3));
        dto.setVmSpecCode("8CORE-64GB");
        dto.setTpsPerCore(35);
        dto.setTpmcPerTps(3000);
        dto.setAvgThreadHoldSec(1.2);
        dto.setThreadMarginRate(1.2);
        dto.setMaxThreadMarginRate(1.3);
        dto.setApType("GENERAL");
        dto.setActiveActive(false);
        dto.setDrValidation(false);
        dto.setValidateDbPool(false);
        dto.setDbSessionLimit(500);
        dto.setAvgDbConnectionHoldSec(0.15);
        dto.setDbTransactionUsageRatio(1.0);
        dto.setPoolSafetyFactor(1.3);
        dto.setThreadDbUsageRatio(0.30);
        dto.setMinPoolPerVm(30);
        return dto;
    }
}
