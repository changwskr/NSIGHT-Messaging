package com.nh.nsight.messaging.xpilotframewrok.as.frameworkas;

import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwTxStatusResponse;
import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwCDtoConverter;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.DCFwFramework;

import org.springframework.stereotype.Service;

/**
 * AS(Application Service) 계층 — 프로그램 ID: MXPFW75001.
 * xpilotFramework 거래(트랜잭션) 상태 조회 비즈니스 로직을 담당한다.
 */
@Service
public class ASMXPFW75001 {

    /** 로그 출력용 AS 프로그램 식별자 */
    private static final String AS = "ASMXPFW75001";

    /** 프레임워크 DC(Data Component) 계층 */
    private final DCFwFramework dcFwFramework;

    public ASMXPFW75001(DCFwFramework dcFwFramework) {
        this.dcFwFramework = dcFwFramework;
    }

    /**
     * guid·거래ID·멱등키로 현재 거래 상태를 조회한다.
     *
     * @param guid           글로벌 고유 식별자
     * @param transactionId  거래 ID
     * @param idempotencyKey 멱등성 키
     * @return AC 응답 DTO로 변환된 거래 상태 (미존재 시 UNKNOWN)
     */
    public FwTxStatusResponse queryStatus(String guid, String transactionId, String idempotencyKey) {
        // START 로그
        System.out.println("★★★★★★★ [" + AS + "] queryStatus START transactionId=" + transactionId);
        // DC 상태 조회 → AC DTO 변환 (null 시 UNKNOWN 기본값)
        FwTxStatusResponse response = FwCDtoConverter.toStatusResponse(
                dcFwFramework.getTxStatus(guid, transactionId, idempotencyKey));
        // END 로그
        System.out.println("★★★★★★★ [" + AS + "] queryStatus END status=" + response.getStatus());
        return response;
    }
}
