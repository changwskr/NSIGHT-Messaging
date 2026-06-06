package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwTxStatusResponse;
import com.nh.nsight.messaging.xpilotframewrok.as.frameworkas.ASMXPFW75001;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AC(Application Controller) 계층 — 프로그램 ID: MXPFW75001.
 * xpilotFramework 거래(트랜잭션) 상태 조회 REST API를 제공한다.
 */
@RestController
@RequestMapping("/api/xpilotframewrok/transaction-status")
public class ACMXPFW75001 {

    /** 로그 출력용 AC 프로그램 식별자 */
    private static final String AC = "ACMXPFW75001";

    /** 거래 상태 조회 비즈니스 로직을 위임하는 AS 계층 */
    private final ASMXPFW75001 asmxpfw75001;

    public ACMXPFW75001(ASMXPFW75001 asmxpfw75001) {
        this.asmxpfw75001 = asmxpfw75001;
    }

    /**
     * 거래 ID·멱등키 등으로 현재 거래 상태를 조회한다.
     *
     * @param body guid, transactionId, idempotencyKey를 담은 요청 본문
     * @return 표준 응답 래퍼에 담긴 거래 상태 정보
     */
    @PostMapping
    public StandardResponse<FwTxStatusResponse> query(@RequestBody Map<String, String> body) {
        // START 로그
        System.out.println("★★★★★★★ [" + AC + "] query START transactionId=" + body.get("transactionId"));
        // AS 계층에 상태 조회 위임
        FwTxStatusResponse response = asmxpfw75001.queryStatus(
                body.get("guid"), body.get("transactionId"), body.get("idempotencyKey"));
        // 표준 성공 응답 조립
        StandardResponse<FwTxStatusResponse> result =
                StandardResponse.success("XPFW-STATUS-001", "xpilotFrameworkTxStatus", response);
        // END 로그
        System.out.println("★★★★★★★ [" + AC + "] query END status=" + response.getStatus());
        return result;
    }
}
