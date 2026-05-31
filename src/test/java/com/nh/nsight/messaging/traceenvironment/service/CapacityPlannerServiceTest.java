package com.nh.nsight.messaging.traceenvironment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nh.nsight.messaging.traceenvironment.model.CapacityPlannerRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CapacityPlannerServiceTest {

    private final CapacityPlannerService service = new CapacityPlannerService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void plan_uses32CoreProfileWhenRequested() {
        var request = new CapacityPlannerRequest(
                "t", 3600, 6, 21600, "32CORE-256GB", false, 0, 0,
                30, 35, 40, 3000, false,
                List.of(5), List.of(3), List.of(60),
                true, true, true, true, 150, 500
        );
        var result = service.plan(request);
        assertThat(result.vmProfileId()).isEqualTo("32CORE-256GB");
        assertThat(result.vmCores()).isEqualTo(32);
        assertThat(result.vmMemoryGb()).isEqualTo(256);
        assertThat(result.vmTpsAt35()).isEqualTo(1120);
        assertThat(result.scenarioLabel()).contains("32코어/256GB");
        assertThat(result.scenarioLabel()).contains("32CORE-256GB");
        var row = result.vmResults().get(0);
        assertThat(row.jvmHeapPerVm()).isEqualTo("32~48 GB");
        assertThat(row.jvmHeapSvPerVm()).isEqualTo("≤64 GB");
        assertThat(row.wasThreadsPerVm()).isEqualTo("1200~1500");
        assertThat(row.dbPoolPerVm()).isEqualTo(150);
        assertThat(row.dbPoolRangeLabel()).isEqualTo("120~150");
        assertThat(row.dbPoolFormula()).contains("120~150").contains("권장 150");
        assertThat(result.tomcatMaxThreadsRange()).isEqualTo("1200~1500");
        assertThat(result.jvmHeapDerivationFormula()).contains("VM당 JVM Heap");
        assertThat(result.wasThreadsDerivationFormula()).contains("Busy_low");
        assertThat(result.hikariPoolFormula()).isEqualTo(row.dbPoolFormula());
        assertThat(row.dbPoolTotal()).isEqualTo(150L * row.recommendedVmActiveActive());
    }

    @Test
    void plan_doesNotTreatSentCustomCoreAsCustomVm() throws Exception {
        String json = """
                {
                  "branchCount":3600,
                  "usersPerBranch":6,
                  "totalUsers":21600,
                  "vmProfileId":"32CORE-256GB",
                  "customVm":false,
                  "customCore":8,
                  "customMemoryGb":64,
                  "tpmcPerTps":3000,
                  "actualRequestPercents":[5],
                  "responseTimeoutSeconds":[3],
                  "sessionIdleMinutes":[60]
                }
                """;
        CapacityPlannerRequest request = mapper.readValue(json, CapacityPlannerRequest.class);
        assertThat(request.customVm()).isFalse();
        assertThat(request.customCore()).isZero();
        assertThat(request.vmProfileId()).isEqualTo("32CORE-256GB");

        var result = service.plan(request);
        assertThat(result.vmCores()).isEqualTo(32);
    }
}
