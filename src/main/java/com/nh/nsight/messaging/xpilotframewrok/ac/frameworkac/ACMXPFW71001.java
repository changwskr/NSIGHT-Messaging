package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwCDtoConverter;
import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwLogResponse;
import com.nh.nsight.messaging.xpilotframewrok.as.frameworkas.ASMXPFW71001;
import com.nh.nsight.messaging.xpilotframewrok.as.frameworkas.ASMXPFW72001;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.dto.FwLogSearchDDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AC(Application Controller) 계층 — 성능(트랜잭션) 로그 REST API.
 * MXPFW71001: 단건 조회, MXPFW72001: 목록 검색.
 */
@RestController
@RequestMapping("/api/xpilotframewrok/performance-logs")
public class ACMXPFW71001 {

    private static final String AC_DETAIL = "ACMXPFW71001";
    private static final String AC_LIST = "ACMXPFW72001";

    private final ASMXPFW71001 asmxpfw71001;
    private final ASMXPFW72001 asmxpfw72001;

    public ACMXPFW71001(ASMXPFW71001 asmxpfw71001, ASMXPFW72001 asmxpfw72001) {
        this.asmxpfw71001 = asmxpfw71001;
        this.asmxpfw72001 = asmxpfw72001;
    }

    @GetMapping("/{logId}")
    public StandardResponse<FwLogResponse> getLog(@PathVariable("logId") Long logId) {
        System.out.println("★★★★★★★ [" + AC_DETAIL + "] getLog START logId=" + logId);
        FwLogResponse response = asmxpfw71001.getPerformanceLog(logId);
        StandardResponse<FwLogResponse> result =
                StandardResponse.success("XPFW-TX-DETAIL-001", "xpilotFrameworkPerfLogDetail", response);
        System.out.println("★★★★★★★ [" + AC_DETAIL + "] getLog END logId=" + logId);
        return result;
    }

    @GetMapping
    public StandardResponse<List<FwLogResponse>> search(
            @RequestParam(value = "guid", required = false) String guid,
            @RequestParam(value = "traceId", required = false) String traceId,
            @RequestParam(value = "serviceId", required = false) String serviceId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "resultCode", required = false) String resultCode,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        System.out.println("★★★★★★★ [" + AC_LIST + "] search START pageNo=" + pageNo);
        FwLogSearchDDTO criteria = FwCDtoConverter.toSearchDDTO(guid, traceId, serviceId, userId, resultCode, null, pageNo, pageSize);
        List<FwLogResponse> rows = asmxpfw72001.searchPerformanceLogs(criteria);
        long total = asmxpfw72001.countPerformanceLogs(criteria);
        StandardResponse<List<FwLogResponse>> result = FwCDtoConverter.toPagedLogResponse(rows, criteria, total);
        System.out.println("★★★★★★★ [" + AC_LIST + "] search END total=" + total);
        return result;
    }
}
