package com.nh.nsight.messaging.traceenvironment.guide;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TomcatWasSizingGuideTest {

    @Test
    void busyThreads_32coreGuideTps() {
        assertThat(TomcatWasSizingGuide.busyThreadsLow(1_000)).isEqualTo(1_200);
        assertThat(TomcatWasSizingGuide.busyThreadsHigh(1_000)).isEqualTo(1_440);
    }

    @Test
    void derivation32_containsBusySteps() {
        String d = TomcatWasSizingGuide.buildDerivationFormula(VmProfile.CORE32_256);
        assertThat(d).contains("Busy_low = ceil(1000 × 1.0 × 1.2) = 1200");
        assertThat(d).contains("maxThreads = §4 1200~1500");
    }

    @Test
    void profile32_maxThreads1200to1500() {
        var spec = VmProfile.CORE32_256.getTomcatHikariSpec();
        assertThat(spec.tomcatMaxThreadsRange()).isEqualTo("1200~1500");
        assertThat(spec.minSpareThreadsMax()).isEqualTo(300);
        assertThat(spec.acceptCountMin()).isEqualTo(500);
        assertThat(spec.keepAliveTimeoutSec()).isEqualTo(60);
        assertThat(spec.maxKeepAliveRequests()).isEqualTo(100);
    }
}
