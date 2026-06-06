package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwAuditLogResponse;
import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwCDtoConverter;
import com.nh.nsight.messaging.xpilotframewrok.as.frameworkas.ASMXPFW74001;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.dto.FwLogSearchDDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AC(Application Controller) 계층 — 프로그램 ID: MXPFW74001.
 * xpilotFramework 감사(Audit) 로그 목록 검색 및 페이징 REST API를 제공한다.
 */
@RestController
@RequestMapping("/api/xpilotframewrok/audit-logs")
public class ACMXPFW74001 {

    /** 로그 출력용 AC 프로그램 식별자 */
    private static final String AC = "ACMXPFW74001";

    /** 감사 로그 검색·건수 조회 비즈니스 로직을 위임하는 AS 계층 */
    private final ASMXPFW74001 asmxpfw74001;

    public ACMXPFW74001(ASMXPFW74001 asmxpfw74001) {
        this.asmxpfw74001 = asmxpfw74001;
    }

    /**
     * 검색 조건에 맞는 감사 로그 목록을 페이징 조회한다.
     *
     * @param guid       글로벌 고유 식별자 (선택)
     * @param userId     사용자 ID (선택)
     * @param actionType 행위 유형 (선택)
     * @param pageNo     페이지 번호 (기본값 1)
     * @param pageSize   페이지 크기 (기본값 20)
     * @return 페이징 메타데이터를 포함한 표준 응답
     */
    @GetMapping
    public StandardResponse<List<FwAuditLogResponse>> search(
            @RequestParam(value = "guid", required = false) String guid,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "actionType", required = false) String actionType,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        // START 로그
        System.out.println("★★★★★★★ [" + AC + "] search START pageNo=" + pageNo);
        // 요청 파라미터 → DC 검색 DTO 변환 (감사 로그 전용 필드: actionType)
        FwLogSearchDDTO criteria = FwCDtoConverter.toSearchDDTO(
                guid, null, null, userId, null, actionType, pageNo, pageSize);
        // AS 계층에 목록·건수 조회 위임
        List<FwAuditLogResponse> rows = asmxpfw74001.searchAuditLogs(criteria);
        long total = asmxpfw74001.countAuditLogs(criteria);
        // 페이징 성공 응답 조립
        StandardResponse<List<FwAuditLogResponse>> result = StandardResponse.successPage(
                "XPFW-AUDIT-LIST-001", "xpilotFrameworkAuditLogList", rows,
                criteria.getSafePageNo(), criteria.getSafePageSize(), total);
        // END 로그
        System.out.println("★★★★★★★ [" + AC + "] search END total=" + total);
        return result;
    }
}
