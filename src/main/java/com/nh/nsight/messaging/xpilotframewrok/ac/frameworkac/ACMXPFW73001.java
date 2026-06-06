package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilotframewrok.as.frameworkas.ASMXPFW73001;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AC(Application Controller) 계층 — 프로그램 ID: MXPFW73001.
 * xpilotFramework 성능(트랜잭션) 로그 삭제 REST API를 제공한다.
 */
@RestController
@RequestMapping("/api/xpilotframewrok/performance-logs")
public class ACMXPFW73001 {

    /** 로그 출력용 AC 프로그램 식별자 */
    private static final String AC = "ACMXPFW73001";

    /** 성능 로그 삭제 비즈니스 로직을 위임하는 AS 계층 */
    private final ASMXPFW73001 asmxpfw73001;

    public ACMXPFW73001(ASMXPFW73001 asmxpfw73001) {
        this.asmxpfw73001 = asmxpfw73001;
    }

    /**
     * 지정한 성능 로그를 삭제한다.
     *
     * @param logId 삭제 대상 로그 PK
     * @return 표준 성공 응답 (본문 없음)
     */
    @DeleteMapping("/{logId}")
    public StandardResponse<Void> delete(@PathVariable Long logId) {
        // START 로그
        System.out.println("★★★★★★★ [" + AC + "] delete START logId=" + logId);
        // AS 계층에 삭제 위임
        asmxpfw73001.deletePerformanceLog(logId);
        // 표준 성공 응답 조립
        StandardResponse<Void> result =
                StandardResponse.success("XPFW-TX-DELETE-001", "xpilotFrameworkPerfLogDelete", null);
        // END 로그
        System.out.println("★★★★★★★ [" + AC + "] delete END logId=" + logId);
        return result;
    }
}
