package com.nh.nsight.messaging.traceenvironment.guide;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DbPoolSizingGuideTest {

    @Test
    void recommend32Core_pool150() {
        var r = DbPoolSizingGuide.recommend(VmProfile.CORE32_256, 32, 256);
        assertThat(r.recommendedGeneral()).isEqualTo(150);
        assertThat(r.rangeLabel()).isEqualTo("120~150");
        assertThat(r.rangeLabel()).isEqualTo("120~150");
        assertThat(r.formulaSummary()).contains("32CORE-256GB");
        assertThat(r.recommendedGeneral()).isEqualTo(150);
        assertThat(r.poolCandidateFromThreads()).isEqualTo(150);
        assertThat(r.derivationFormula()).contains("floor(1500 × 10%) = 150");
        assertThat(r.derivationFormula()).contains("VM당 DB Pool 권장 = 150");
    }

    @Test
    void derivation16Core_candidate100() {
        var r = DbPoolSizingGuide.recommend(VmProfile.CORE16_64, 16, 64);
        assertThat(r.poolCandidateFromThreads()).isEqualTo(100);
        assertThat(r.derivationFormula()).contains("clamp(100, 80, 100) = 100");
    }

    @Test
    void recommend16Core_pool80to100() {
        var r = DbPoolSizingGuide.recommend(VmProfile.CORE16_64, 16, 64);
        assertThat(r.rangeLabel()).isEqualTo("80~100");
        assertThat(r.recommendedGeneral()).isEqualTo(100);
    }

    @Test
    void recommend8Core_pool80to100() {
        var r = DbPoolSizingGuide.recommend(VmProfile.CORE8_32, 8, 32);
        assertThat(r.recommendedGeneral()).isEqualTo(100);
    }
}
