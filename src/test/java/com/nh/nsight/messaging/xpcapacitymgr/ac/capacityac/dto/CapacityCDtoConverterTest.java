package com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac.dto;

import com.nh.nsight.messaging.traceenvironment.guide.VmProfile;
import com.nh.nsight.messaging.xpcapacitymgr.support.XpcapacityTestFixtures;
import com.nh.nsight.messaging.xpcapacitymgr.util.XpCapacityBizException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CapacityCDtoConverterTest {

    @Test
    void defaultRequest_hasStandardScenarioFields() {
        CapacityCalculationCDTO dto = CapacityCDtoConverter.defaultRequest();

        assertThat(dto.getProjectName()).contains("6,000지점");
        assertThat(dto.resolvedTotalUsers()).isEqualTo(36_000);
        assertThat(dto.getConcurrentRequestRates()).containsExactly(0.03, 0.05, 0.10, 0.15);
        assertThat(dto.getVmSpecCode()).isEqualTo("8C64G");
    }

    @Test
    void toDomain_mapsVmProfileAndDesignedSessions() {
        var domain = CapacityCDtoConverter.toDomain(XpcapacityTestFixtures.minimalCalculationRequest());

        assertThat(domain.totalUsers()).isEqualTo(1_000);
        assertThat(domain.designedSessions()).isEqualTo(1_100);
        assertThat(domain.vmProfileId()).isEqualTo("8CORE-64GB");
        assertThat(domain.vmCores()).isEqualTo(8);
        assertThat(domain.vmTpsAtBase()).isEqualTo(8 * 35);
    }

    @Test
    void resolveVmProfile_acceptsAliases() {
        assertThat(CapacityCDtoConverter.resolveVmProfile("32C256G")).isEqualTo(VmProfile.CORE32_256);
        assertThat(CapacityCDtoConverter.resolveVmProfile("8CORE-64GB")).isEqualTo(VmProfile.CORE8_64);
    }

    @Test
    void toDomain_singleView_usesHigherDbHoldDefault() {
        CapacityCalculationCDTO dto = XpcapacityTestFixtures.minimalCalculationRequest();
        dto.setApType("SINGLE_VIEW");
        dto.setAvgDbConnectionHoldSec(0);

        assertThat(CapacityCDtoConverter.toDomain(dto).avgDbConnectionHoldSec()).isEqualTo(0.20);
    }

    @Test
    void toDomain_nullRequest_throws() {
        assertThatThrownBy(() -> CapacityCDtoConverter.toDomain(null))
                .isInstanceOf(XpCapacityBizException.class)
                .hasMessageContaining("비어 있습니다");
    }

    @Test
    void toDomain_zeroUsers_throws() {
        CapacityCalculationCDTO dto = XpcapacityTestFixtures.minimalCalculationRequest();
        dto.setBranchCount(0);
        dto.setUserPerBranch(0);

        assertThatThrownBy(() -> CapacityCDtoConverter.toDomain(dto))
                .isInstanceOf(XpCapacityBizException.class)
                .hasMessageContaining("전체 사용자");
    }

    @Test
    void toDomain_emptyRates_usesDefaults() {
        CapacityCalculationCDTO dto = XpcapacityTestFixtures.minimalCalculationRequest();
        dto.setConcurrentRequestRates(java.util.List.of());

        var domain = CapacityCDtoConverter.toDomain(dto);

        assertThat(domain.concurrentRequestRates()).containsExactly(0.03, 0.05, 0.10, 0.15);
    }
}
