package com.nh.nsight.messaging.xpcapacitymgr.as.capacityas;

import com.nh.nsight.messaging.traceenvironment.guide.VmProfile;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.WasThreadOnlyCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.WasThreadResultCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.dc.capacitydc.DCCapacity;
import com.nh.nsight.messaging.xpcapacitymgr.util.XpCapacityBizException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ASMSC72001Test {

    @Mock
    private DCCapacity dcCapacity;

    private ASMSC72001 service;

    @BeforeEach
    void setUp() {
        service = new ASMSC72001(dcCapacity);
    }

    @Test
    void calculateWasThread_nullRequest_throws() {
        assertThatThrownBy(() -> service.calculateWasThread(null))
                .isInstanceOf(XpCapacityBizException.class)
                .hasMessageContaining("목표 TPS");
    }

    @Test
    void calculateWasThread_zeroTps_throws() {
        WasThreadOnlyCDTO request = new WasThreadOnlyCDTO();
        request.setTargetTps(0);

        assertThatThrownBy(() -> service.calculateWasThread(request))
                .isInstanceOf(XpCapacityBizException.class);
    }

    @Test
    void calculateWasThread_delegatesToDc() {
        WasThreadOnlyCDTO request = new WasThreadOnlyCDTO();
        request.setTargetTps(300);
        request.setApCount(2);
        request.setVmSpecCode("8CORE-64GB");
        request.setAvgThreadHoldSec(1.2);
        request.setThreadMarginRate(1.2);
        request.setMaxThreadMarginRate(1.3);

        WasThreadResultCDTO expected = new WasThreadResultCDTO();
        expected.setStatus("NORMAL");
        when(dcCapacity.calculateWasThreadOnly(
                eq(300), eq(2), eq(1.2), eq(1.2), eq(1.3), eq(VmProfile.CORE8_64)))
                .thenReturn(expected);

        WasThreadResultCDTO result = service.calculateWasThread(request);

        assertThat(result.getStatus()).isEqualTo("NORMAL");
        verify(dcCapacity).calculateWasThreadOnly(300, 2, 1.2, 1.2, 1.3, VmProfile.CORE8_64);
    }

    @Test
    void calculateWasThread_apCountBelowOne_usesOne() {
        WasThreadOnlyCDTO request = new WasThreadOnlyCDTO();
        request.setTargetTps(100);
        request.setApCount(0);
        request.setVmSpecCode("8C64G");

        when(dcCapacity.calculateWasThreadOnly(
                anyInt(), eq(1), anyDouble(), anyDouble(), anyDouble(), eq(VmProfile.CORE8_64)))
                .thenReturn(new WasThreadResultCDTO());

        service.calculateWasThread(request);

        verify(dcCapacity).calculateWasThreadOnly(eq(100), eq(1), anyDouble(), anyDouble(), anyDouble(),
                eq(VmProfile.CORE8_64));
    }
}
