package com.nh.nsight.messaging.xpilotframewrok.as.frameworkas;

import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwLogResponse;
import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwCDtoConverter;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.DCFwFramework;
import com.nh.nsight.messaging.xpilotframewrok.common.exception.FwFrameworkException;

import org.springframework.stereotype.Service;

/**
 * AS(Application Service) 계층 — 프로그램 ID: MXPFW71001.
 * xpilotFramework 성능(트랜잭션) 로그 단건 조회 비즈니스 로직을 담당한다.
 */
@Service
public class ASMXPFW71001 {

    /** 로그 출력용 AS 프로그램 식별자 */
    private static final String AS = "ASMXPFW71001";

    /** 프레임워크 DC(Data Component) 계층 */
    private final DCFwFramework dcFwFramework;

    public ASMXPFW71001(DCFwFramework dcFwFramework) {
        this.dcFwFramework = dcFwFramework;
    }

    /**
     * logId로 성능 로그 단건을 조회한다. 미존재 시 비즈니스 예외를 발생시킨다.
     *
     * @param logId 조회 대상 로그 PK
     * @return AC 응답 DTO로 변환된 성능 로그
     * @throws FwFrameworkException 로그가 존재하지 않을 때 (BIZ-001)
     */
    public FwLogResponse getPerformanceLog(Long logId) {
        // START 로그
        System.out.println("★★★★★★★ [" + AS + "] getPerformanceLog START logId=" + logId);
        // DC 계층에서 트랜잭션 로그 조회
        var row = dcFwFramework.getTxLog(logId);
        // 미존재 검증
        if (row == null) {
            throw new FwFrameworkException("BIZ-001", "logId=" + logId);
        }
        // DC → AC DTO 변환
        FwLogResponse response = FwCDtoConverter.toLogResponse(row);
        // END 로그
        System.out.println("★★★★★★★ [" + AS + "] getPerformanceLog END logId=" + logId);
        return response;
    }
}
