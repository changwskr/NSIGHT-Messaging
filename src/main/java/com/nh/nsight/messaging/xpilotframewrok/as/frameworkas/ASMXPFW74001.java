package com.nh.nsight.messaging.xpilotframewrok.as.frameworkas;

import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwAuditLogResponse;
import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwCDtoConverter;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.DCFwFramework;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.dto.FwLogSearchDDTO;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AS(Application Service) 계층 — 프로그램 ID: MXPFW74001.
 * xpilotFramework 감사(Audit) 로그 목록 검색 및 건수 조회 비즈니스 로직을 담당한다.
 */
@Service
public class ASMXPFW74001 {

    /** 로그 출력용 AS 프로그램 식별자 */
    private static final String AS = "ASMXPFW74001";

    /** 프레임워크 DC(Data Component) 계층 */
    private final DCFwFramework dcFwFramework;

    public ASMXPFW74001(DCFwFramework dcFwFramework) {
        this.dcFwFramework = dcFwFramework;
    }

    /**
     * 검색 조건에 맞는 감사 로그 목록을 조회한다.
     *
     * @param criteria DC 검색 조건 DTO
     * @return AC 응답 DTO 목록
     */
    public List<FwAuditLogResponse> searchAuditLogs(FwLogSearchDDTO criteria) {
        // START 로그
        System.out.println("★★★★★★★ [" + AS + "] searchAuditLogs START pageNo=" + criteria.getSafePageNo());
        // DC 목록 조회 → AC DTO 변환
        List<FwAuditLogResponse> result = FwCDtoConverter.toAuditResponseList(dcFwFramework.listAuditLogs(criteria));
        // END 로그
        System.out.println("★★★★★★★ [" + AS + "] searchAuditLogs END size=" + result.size());
        return result;
    }

    /**
     * 검색 조건에 맞는 감사 로그 전체 건수를 조회한다.
     *
     * @param criteria DC 검색 조건 DTO
     * @return 조건에 해당하는 전체 건수
     */
    public long countAuditLogs(FwLogSearchDDTO criteria) {
        // START 로그
        System.out.println("★★★★★★★ [" + AS + "] countAuditLogs START");
        // DC 건수 조회
        long total = dcFwFramework.countAuditLogs(criteria);
        // END 로그
        System.out.println("★★★★★★★ [" + AS + "] countAuditLogs END total=" + total);
        return total;
    }
}
