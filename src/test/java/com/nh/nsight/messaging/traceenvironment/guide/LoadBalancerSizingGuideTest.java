package com.nh.nsight.messaging.traceenvironment.guide;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoadBalancerSizingGuideTest {

    @Test
    void spec32Core_idle70to90_andLeastConn() {
        var lb = LoadBalancerSizingGuide.specFor(VmProfile.CORE32_256, 60);
        assertThat(lb.clientL4IdleRange()).isEqualTo("70~90초");
        assertThat(lb.clientL4IdleSecDefault()).isEqualTo(80);
        assertThat(lb.loadBalancingMethod()).contains("Least Connection");
        assertThat(lb.healthFailCountRange()).isEqualTo("2~3");
        assertThat(lb.stickyTimeoutRange()).isEqualTo("70~80분");
        assertThat(lb.maxConnectionGuidance()).contains("30000");
    }

    @Test
    void spec16Core_uses120sIdle() {
        var lb = LoadBalancerSizingGuide.specFor(VmProfile.CORE16_64, 60);
        assertThat(lb.clientL4IdleRange()).isEqualTo("120초");
    }
}
