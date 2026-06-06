package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwProcessRequestCDTO;
import com.nh.nsight.messaging.xpilotframewrok.as.frameworkas.ASMXPFW76001;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AC(Application Controller) 계층 — 프로그램 ID: MXPFW76001.
 * xpilotFramework 거래 처리(전처리·업무·후처리 파이프라인) REST API를 제공한다.
 */
@RestController
@RequestMapping("/api/xpilotframewrok/process")
public class ACMXPFW76001 {

    private static final String AC = "ACMXPFW76001";

    private final ASMXPFW76001 asmxpfw76001;

    public ACMXPFW76001(ASMXPFW76001 asmxpfw76001) {
        this.asmxpfw76001 = asmxpfw76001;
    }

    @PostMapping
    public StandardResponse<Map<String, Object>> process(@RequestBody FwProcessRequestCDTO request) {
        System.out.println("★★★★★★★ [" + AC + "] process START transactionId=" + request.getTransactionId());
        Map<String, Object> body = asmxpfw76001.process(request);
        StandardResponse<Map<String, Object>> processResult =
                StandardResponse.success("XPFW-PROCESS-001", "xpilotFrameworkProcess", body);
        System.out.println("★★★★★★★ [" + AC + "] process END transactionId=" + request.getTransactionId());
        return processResult;
    }
}
