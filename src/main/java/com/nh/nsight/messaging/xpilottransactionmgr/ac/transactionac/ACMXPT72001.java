package com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogResponse;
import com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac.dto.TransactionLogCDtoConverter;
import com.nh.nsight.messaging.xpilottransactionmgr.as.transactionas.ASMXPT72001;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.dto.TransactionLogSearchDDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/xpilottransactionmgr/transaction-logs")
public class ACMXPT72001 {

    private static final String AC = "ACMXPT72001";

    private final ASMXPT72001 asmxpt72001;

    public ACMXPT72001(ASMXPT72001 asmxpt72001) {
        this.asmxpt72001 = asmxpt72001;
    }

    @GetMapping
    public StandardResponse<List<TransactionLogResponse>> searchLogs(
            @RequestParam(required = false) String guid,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String serviceId,
            @RequestParam(required = false) String resultCode,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "3") int pageSize
    ) {
        System.out.println("★★★★★★★ [" + AC + "] searchLogs START pageNo=" + pageNo);
        TransactionLogSearchDDTO condition = TransactionLogCDtoConverter.toSearchDDTO(
                guid, traceId, transactionId, serviceId, resultCode, userId, pageNo, pageSize);
        List<TransactionLogResponse> rows = asmxpt72001.searchLogs(condition);
        long total = asmxpt72001.countLogs(condition);
        StandardResponse<List<TransactionLogResponse>> result = StandardResponse.successPage(
                "XPT-TX-LIST-001", "xpilotTransactionLogList", rows,
                condition.getSafePageNo(), condition.getSafePageSize(), total);
        System.out.println("★★★★★★★ [" + AC + "] searchLogs END total=" + total);
        return result;
    }
}
