package com.nh.nsight.messaging.junmun.util;

import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunBuildRequestCDTO;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunCDtoConverter;
import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JunmunJsonEnvelopeBuilderTest {

    @Test
    void build_ph1Sample_containsHeaderAndBody() {
        JunmunDefinitionDDTO def = JunmunCDtoConverter.toDomain(JunmunCDtoConverter.defaultPh1Request());
        JunmunBuildRequestCDTO req = new JunmunBuildRequestCDTO();
        req.setFieldValues(Map.of(
                "TGM_LEN", "00000504",
                "GLBL_ID", "20260603-TEST-0001",
                "TRN_DT", "20260603",
                "TRN_TM", "120000",
                "TRN_ID", "INB_ACCT_INQ_001",
                "SRVC_ID", "accountInquiry",
                "BRN_NO", "0001",
                "USER_ID", "TESTUSER",
                "ACNO", "9999999999999",
                "INQ_DVCD", "01"
        ));

        String json = JunmunJsonEnvelopeBuilder.build(def, req);

        assertThat(json).contains("\"header\"");
        assertThat(json).contains("\"system\"");
        assertThat(json).contains("\"ACNO\"");
        assertThat(json).contains("9999999999999");
        assertThat(JunmunJsonEnvelopeBuilder.validate(def.layoutJson(), json)).isEmpty();
    }
}
