package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto;

import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxLog;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxStatus;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FwCDtoConverterTest {

    @Test
    void toStatusResponse_null_returnsUnknown() {
        FwTxStatusResponse res = FwCDtoConverter.toStatusResponse(null);

        assertThat(res.getStatus()).isEqualTo("UNKNOWN");
        assertThat(res.getRetryAllowedYn()).isEqualTo("Y");
    }

    @Test
    void toStatusResponse_success_setsRetryNotAllowed() {
        FwTxStatus row = new FwTxStatus();
        row.setStatus("SUCCESS");
        row.setResultCode("COM-0000");

        FwTxStatusResponse res = FwCDtoConverter.toStatusResponse(row);

        assertThat(res.getStatus()).isEqualTo("SUCCESS");
        assertThat(res.getRetryAllowedYn()).isEqualTo("N");
    }

    @Test
    void toLogResponse_mapsFields() {
        FwTxLog row = new FwTxLog();
        row.setLogId(1L);
        row.setGuid("G-001");
        row.setServiceId("xpilotFrameworkProcess");
        row.setResultCode("COM-0000");
        row.setTotalTime(42L);

        FwLogResponse res = FwCDtoConverter.toLogResponse(row);

        assertThat(res.getLogId()).isEqualTo(1L);
        assertThat(res.getGuid()).isEqualTo("G-001");
        assertThat(res.getTotalTime()).isEqualTo(42L);
    }
}
