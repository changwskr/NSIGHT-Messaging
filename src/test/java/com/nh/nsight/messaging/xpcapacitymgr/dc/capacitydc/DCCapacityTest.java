package com.nh.nsight.messaging.xpcapacitymgr.dc.capacitydc;

import com.nh.nsight.messaging.traceenvironment.guide.NsightCapacityDerivation;
import com.nh.nsight.messaging.traceenvironment.guide.VmProfile;
import com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto.CapacityCDtoConverter;
import com.nh.nsight.messaging.xpcapacitymgr.dc.capacitydc.dto.CapacityCalculationDDTO;
import com.nh.nsight.messaging.xpcapacitymgr.support.XpcapacityTestFixtures;
import com.nh.nsight.messaging.xpcapacitymgr.util.CapacityCalcStep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DCCapacityTest {

    private DCCapacity dcCapacity;
    private CapacityCalculationDDTO input;

    @BeforeEach
    void setUp() {
        dcCapacity = new DCCapacity();
        input = CapacityCDtoConverter.toDomain(XpcapacityTestFixtures.minimalCalculationRequest());
    }

    @Test
    void calculate_cap020_tpsOnly_noWasOrDb() {
        var result = dcCapacity.calculate(input, CapacityCalcStep.CAP_020);

        assertThat(result.getCalculatedStep()).isEqualTo("020");
        assertThat(result.getResults()).hasSize(1);
        var row = result.getResults().get(0);
        assertThat(row.getConcurrentRequestUsers()).isEqualTo(50);
        assertThat(row.getTargetTps()).isEqualTo(17);
        assertThat(row.getWasThread()).isNull();
        assertThat(row.getDbPool()).isNull();
        assertThat(row.getRequiredApCount()).isZero();
    }

    @Test
    void calculate_all_includesWasAndDb() {
        var result = dcCapacity.calculate(input, CapacityCalcStep.ALL);

        assertThat(result.getScenarioId()).startsWith("CAP-");
        assertThat(result.getResults()).hasSize(1);
        var row = result.getResults().get(0);
        assertThat(row.getWasThread()).isNotNull();
        assertThat(row.getDbPool()).isNotNull();
        assertThat(row.getRecommendedApCount()).isGreaterThan(0);
        assertThat(result.getRiskSummary()).containsKeys("normal", "warning", "critical");
        assertThat(result.getSummaryFormula()).contains("DB Pool");
    }

    @Test
    void calculateWasThreadOnly_returnsThreadMetrics() {
        var was = dcCapacity.calculateWasThreadOnly(
                600,
                2,
                1.2,
                1.2,
                1.3,
                VmProfile.CORE8_64);

        assertThat(was.getTotalCalculatedThreads()).isEqualTo(864);
        assertThat(was.getThreadsPerVm()).isEqualTo(432);
        assertThat(was.getRecommendedMaxThreads()).isGreaterThan(was.getThreadsPerVm());
        assertThat(was.getStatus()).isIn("NORMAL", "WARN", "CRITICAL");
        assertThat(was.getStatusMessage()).contains("CAP-WAS");
    }

    @Test
    void calculate_peakTps_matchesDerivationFormula() {
        int concurrentUsers = NsightCapacityDerivation.expectedActualRequestFromPercent(
                input.totalUsers(), 5);
        int expectedTps = NsightCapacityDerivation.peakTpsFromActualRequestUsers(concurrentUsers, 3000);

        var row = dcCapacity.calculate(input, CapacityCalcStep.CAP_020).getResults().get(0);

        assertThat(row.getConcurrentRequestUsers()).isEqualTo(concurrentUsers);
        assertThat(row.getTargetTps()).isEqualTo(expectedTps);
    }
}
