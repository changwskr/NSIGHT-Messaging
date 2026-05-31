package com.nh.nsight.messaging.traceenvironment.guide;

import com.nh.nsight.messaging.traceenvironment.model.JvmSizingRecommendation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JvmSizingGuideTest {

    @Test
    void eightGbPerCore_8x64_heapFromCoreFormula() {
        JvmSizingRecommendation r = JvmSizingGuide.recommend(8, 64, "8CORE-64GB");
        assertThat(r.heapGeneralMinGb()).isEqualTo(12);
        assertThat(r.heapGeneralMaxGb()).isEqualTo(14);
        assertThat(r.heapSingleViewMaxGb()).isEqualTo(28);
        assertThat(JvmSizingGuide.isEightGbPerCore(8, 64)).isTrue();
    }

    @Test
    void eightGbPerCore_16x128() {
        JvmSizingRecommendation r = JvmSizingGuide.recommend(16, 128, "16CORE-128GB");
        assertThat(r.heapGeneralMinGb()).isEqualTo(24);
        assertThat(r.heapGeneralMaxGb()).isEqualTo(28);
        assertThat(r.heapSingleViewMaxGb()).isEqualTo(40);
    }

    @Test
    void derivation32Core_containsHeapSteps() {
        String d = JvmSizingGuide.buildDerivationFormula(32, 256, "32CORE-256GB");
        assertThat(d).contains("32CORE-256GB");
        assertThat(d).contains("VM당 JVM Heap = 32~48 GB");
        assertThat(d).contains("round(32×1.5)");
    }

    @Test
    void recommend32CoreMatchesGuideConstants() {
        JvmSizingRecommendation r = JvmSizingGuide.recommend(VmProfile.CORE32_256);
        assertThat(r.heapGeneralMinGb()).isEqualTo(Nsight32Core256GbGuide.JVM_HEAP_GENERAL_GB_MIN);
        assertThat(r.heapGeneralMaxGb()).isEqualTo(Nsight32Core256GbGuide.JVM_HEAP_GENERAL_GB_MAX);
        assertThat(r.heapSingleViewMaxGb()).isEqualTo(Nsight32Core256GbGuide.JVM_HEAP_SINGLEVIEW_GB_MAX);
    }

    @Test
    void onlineStandard_8core32gb() {
        JvmSizingRecommendation r = JvmSizingGuide.recommend(VmProfile.CORE8_32);
        assertThat(r.heapGeneralMinGb()).isEqualTo(12);
        assertThat(r.heapGeneralMaxGb()).isEqualTo(14);
        assertThat(r.heapSingleViewMaxGb()).isEqualTo(14);
        assertThat(JvmSizingGuide.isEightGbPerCore(8, 32)).isFalse();
    }

    @Test
    void custom12core96gb_eightGbPerCore() {
        JvmSizingRecommendation r = JvmSizingGuide.recommend(12, 96, null);
        assertThat(r.heapGeneralMinGb()).isEqualTo(18);
        assertThat(r.heapGeneralMaxGb()).isEqualTo(21);
    }
}
