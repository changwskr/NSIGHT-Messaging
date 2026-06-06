package com.nh.nsight.messaging.xpilotframewrok.as.frameworkas;

import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.DCFwFramework;

import org.springframework.stereotype.Service;

/**
 * AS(Application Service) 계층 — 프로그램 ID: MXPFW73001.
 * xpilotFramework 성능(트랜잭션) 로그 삭제 비즈니스 로직을 담당한다.
 */
@Service
public class ASMXPFW73001 {

    /** 로그 출력용 AS 프로그램 식별자 */
    private static final String AS = "ASMXPFW73001";

    /** 프레임워크 DC(Data Component) 계층 */
    private final DCFwFramework dcFwFramework;

    public ASMXPFW73001(DCFwFramework dcFwFramework) {
        this.dcFwFramework = dcFwFramework;
    }

    /**
     * 지정한 성능 로그를 삭제한다.
     *
     * @param logId 삭제 대상 로그 PK
     */
    public void deletePerformanceLog(Long logId) {
        // START 로그
        System.out.println("★★★★★★★ [" + AS + "] deletePerformanceLog START logId=" + logId);
        // DC 계층에 삭제 위임
        dcFwFramework.deleteTxLog(logId);
        // END 로그
        System.out.println("★★★★★★★ [" + AS + "] deletePerformanceLog END logId=" + logId);
    }
}
