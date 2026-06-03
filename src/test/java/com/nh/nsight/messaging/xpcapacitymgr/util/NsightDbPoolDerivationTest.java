package com.nh.nsight.messaging.xpcapacitymgr.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NsightDbPoolDerivationTest {

    @Test
    void defaultHoldSec_singleViewAndGeneral() {
        assertThat(NsightDbPoolDerivation.defaultHoldSec(false))
                .isEqualTo(NsightDbPoolDerivation.DEFAULT_DB_HOLD_SEC_GENERAL);
        assertThat(NsightDbPoolDerivation.defaultHoldSec(true))
                .isEqualTo(NsightDbPoolDerivation.DEFAULT_DB_HOLD_SEC_SINGLE_VIEW);
    }

    @Test
    void recommend_appliesMinPoolAndFormulaSteps() {
        var result = NsightDbPoolDerivation.recommend(new NsightDbPoolDerivation.Input(
                100.0,
                200,
                0.15,
                1.0,
                1.3,
                0.30,
                30,
                250
        ));

        assertThat(result.theoreticalPool()).isEqualTo(20);
        assertThat(result.ceilingPool()).isEqualTo(60);
        assertThat(result.sizedPool()).isEqualTo(20);
        assertThat(result.recommendedPool()).isEqualTo(30);
        assertThat(result.apTpsRounded()).isEqualTo(100);
        assertThat(result.formulaSummary()).contains("②산출→20").contains("④배포=max(30,⑤)=30");
    }

    @Test
    void recommend_respectsProfileCap() {
        var result = NsightDbPoolDerivation.recommend(new NsightDbPoolDerivation.Input(
                500.0,
                100,
                0.15,
                1.0,
                1.3,
                0.30,
                50,
                40
        ));

        assertThat(result.sizedPool()).isEqualTo(30);
        assertThat(result.recommendedPool()).isEqualTo(40);
    }
}
