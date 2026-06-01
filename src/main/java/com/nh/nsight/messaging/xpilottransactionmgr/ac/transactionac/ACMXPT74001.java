package com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogDeleteResponse;
import com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac.dto.TransactionLogCDtoConverter;
import com.nh.nsight.messaging.xpilottransactionmgr.as.transactionas.ASMXPT74001;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.dto.TransactionLogSearchDDTO;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xpilottransactionmgr/transaction-logs")
public class ACMXPT74001 {

    private static final String AC = "ACMXPT74001";

    private final ASMXPT74001 asmxpt74001;

    public ACMXPT74001(ASMXPT74001 asmxpt74001) {
        this.asmxpt74001 = asmxpt74001;
    }

    @DeleteMapping
    public StandardResponse<TransactionLogDeleteResponse> deleteLogsByCondition(
            @RequestParam(required = false) String guid,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String serviceId,
            @RequestParam(required = false) String resultCode,
            @RequestParam(required = false) String userId
    ) {
        System.out.println("★★★★★★★ [" + AC + "] deleteLogsByCondition START");
        TransactionLogSearchDDTO condition = TransactionLogCDtoConverter.toSearchDDTO(
                guid, traceId, transactionId, serviceId, resultCode, userId, null, null);
        TransactionLogDeleteResponse response = asmxpt74001.deleteLogsByCondition(condition);
        StandardResponse<TransactionLogDeleteResponse> result =
                StandardResponse.success("XPT-TX-DELETE-002", "xpilotTransactionLogBulkDelete", response);
        System.out.println("★★★★★★★ [" + AC + "] deleteLogsByCondition END");
        return result;
    }
}
