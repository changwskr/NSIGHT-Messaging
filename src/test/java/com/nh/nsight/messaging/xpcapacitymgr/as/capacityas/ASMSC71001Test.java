package com.nh.nsight.messaging.xpcapacitymgr.as.capacityas;

import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCalculationCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCalculationResultCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.dc.capacitydc.DCCapacity;
import com.nh.nsight.messaging.xpcapacitymgr.dc.capacitydc.dto.CapacityCalculationDDTO;
import com.nh.nsight.messaging.xpcapacitymgr.support.XpcapacityTestFixtures;
import com.nh.nsight.messaging.xpcapacitymgr.util.CapacityCalcStep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ASMSC71001Test {

    @Mock
    private DCCapacity dcCapacity;

    private ASMSC71001 service;

    @BeforeEach
    void setUp() {
        service = new ASMSC71001(dcCapacity);
    }

    @Test
    void defaults_delegatesToConverter() {
        CapacityCalculationCDTO defaults = service.defaults();

        assertThat(defaults.resolvedTotalUsers()).isEqualTo(36_000);
        assertThat(defaults.getVmSpecCode()).isEqualTo("8C64G");
    }

    @Test
    void calculate_passesStepToDc() {
        CapacityCalculationCDTO request = XpcapacityTestFixtures.minimalCalculationRequest();
        request.setCalculationStep("040");

        CapacityCalculationResultCDTO expected = new CapacityCalculationResultCDTO();
        expected.setScenarioId("CAP-TEST");
        when(dcCapacity.calculate(any(CapacityCalculationDDTO.class), eq(CapacityCalcStep.CAP_040)))
                .thenReturn(expected);

        CapacityCalculationResultCDTO result = service.calculate(request);

        assertThat(result.getScenarioId()).isEqualTo("CAP-TEST");
        ArgumentCaptor<CapacityCalcStep> stepCaptor = ArgumentCaptor.forClass(CapacityCalcStep.class);
        verify(dcCapacity).calculate(any(CapacityCalculationDDTO.class), stepCaptor.capture());
        assertThat(stepCaptor.getValue()).isEqualTo(CapacityCalcStep.CAP_040);
    }
}
