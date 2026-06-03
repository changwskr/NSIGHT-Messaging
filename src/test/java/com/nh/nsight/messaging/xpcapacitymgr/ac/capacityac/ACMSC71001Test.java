package com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCalculationCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCalculationResultCDTO;
import com.nh.nsight.messaging.xpcapacitymgr.as.capacityas.ASMSC71001;
import com.nh.nsight.messaging.xpcapacitymgr.support.XpcapacityTestFixtures;
import com.nh.nsight.messaging.xpcapacitymgr.util.XpCapacityBizException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ACMSC71001Test {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ASMSC71001 asmsc71001;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ACMSC71001(asmsc71001)).build();
    }

    @Test
    void defaults_returnsOkPayload() throws Exception {
        when(asmsc71001.defaults()).thenReturn(XpcapacityTestFixtures.minimalCalculationRequest());

        mockMvc.perform(get("/api/xpcapacitymgr/defaults"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectName").value("테스트 시나리오"));
    }

    @Test
    void calculate_returnsScenarioResult() throws Exception {
        CapacityCalculationResultCDTO result = new CapacityCalculationResultCDTO();
        result.setScenarioId("CAP-20260603-120000");
        when(asmsc71001.calculate(any(CapacityCalculationCDTO.class))).thenReturn(result);

        CapacityCalculationCDTO body = XpcapacityTestFixtures.minimalCalculationRequest();

        mockMvc.perform(post("/api/xpcapacitymgr/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.scenarioId").value("CAP-20260603-120000"));
    }

    @Test
    void calculateStep_missingStep_returnsBadRequest() throws Exception {
        CapacityCalculationCDTO body = XpcapacityTestFixtures.minimalCalculationRequest();
        body.setCalculationStep("");

        mockMvc.perform(post("/api/xpcapacitymgr/calculate-step")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void handleBiz_returnsBadRequest() throws Exception {
        when(asmsc71001.calculate(any()))
                .thenThrow(new XpCapacityBizException("검증 오류"));

        mockMvc.perform(post("/api/xpcapacitymgr/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                XpcapacityTestFixtures.minimalCalculationRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("검증 오류"));
    }
}
