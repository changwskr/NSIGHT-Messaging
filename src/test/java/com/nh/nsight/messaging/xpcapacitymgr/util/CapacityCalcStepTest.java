package com.nh.nsight.messaging.xpcapacitymgr.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CapacityCalcStepTest {

    @ParameterizedTest
    @CsvSource({
            "020, CAP_020, false, false, false",
            "030, CAP_030, true, false, false",
            "040, CAP_040, true, true, false",
            "050, CAP_050, true, true, true",
            "ALL, ALL, true, true, true",
            "CAP-040, CAP_040, true, true, false",
    })
    void resolve_parsesStepCodes(String raw, CapacityCalcStep expected,
                                 boolean ap, boolean was, boolean db) {
        CapacityCalcStep step = CapacityCalcStep.resolve(raw);
        assertThat(step).isEqualTo(expected);
        assertThat(step.includesAp()).isEqualTo(ap);
        assertThat(step.includesWas()).isEqualTo(was);
        assertThat(step.includesDb()).isEqualTo(db);
    }

    @Test
    void resolve_blank_returnsAll() {
        assertThat(CapacityCalcStep.resolve(null)).isEqualTo(CapacityCalcStep.ALL);
        assertThat(CapacityCalcStep.resolve("  ")).isEqualTo(CapacityCalcStep.ALL);
    }

    @Test
    void resolve_unknown_throwsBizException() {
        assertThatThrownBy(() -> CapacityCalcStep.resolve("999"))
                .isInstanceOf(XpCapacityBizException.class)
                .hasMessageContaining("지원하지 않는 산정 단계");
    }
}
