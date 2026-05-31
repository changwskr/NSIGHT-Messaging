package com.nh.nsight.messaging.traceenvironment.guide;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpringBootSizingGuideTest {

    @Test
    void profile32_asyncAndTx() {
        var spec = SpringBootSizingGuide.specFor(VmProfile.CORE32_256);
        assertThat(spec.asyncCorePoolRange()).isEqualTo("50~100");
        assertThat(spec.asyncMaxPoolRange()).isEqualTo("100~200");
        assertThat(spec.transactionTimeoutRange()).isEqualTo("4~5s");
        assertThat(spec.tomcatMaxThreadsRange()).isEqualTo("1200~1500");
        assertThat(spec.keepAliveTimeoutSec()).isEqualTo(60);
    }
}
