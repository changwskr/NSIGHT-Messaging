package com.nh.nsight.messaging.junmun.util;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 차아키 PH1 내부표준전문 기본 레이아웃·샘플 (2008 AA 아키텍처 확립단계 자료 기반).
 */
public final class JunmunPh1StandardCatalog {

    public static final String DEFAULT_MESSAGE_CODE = "INB_ACCT_INQ_001";
    public static final String DEFAULT_TRANSACTION_ID = "INB_ACCT_INQ_001";
    public static final String DEFAULT_SERVICE_ID = "accountInquiry";
    public static final String STANDARD_VERSION = "PH1-20080421";
    public static final String DOCUMENT_REF =
            "차아키_AA_PH1_내부표준전문_20080421_v1.0 / 차아키_표준화_PH1_차세대표준화지침서_20080304_v1.0";

    private JunmunPh1StandardCatalog() {
    }

    public static String defaultLayoutJson() {
        return readClasspath("junmun/ph1-default-layout.json");
    }

    public static String defaultSampleEnvelopeJson() {
        return readClasspath("junmun/ph1-sample-envelope.json");
    }

    private static String readClasspath(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new JunmunBizException("PH1 기준 리소스를 읽을 수 없습니다: " + path);
        }
    }
}
