package com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogResponse;
import com.nh.nsight.messaging.xpilottransactionmgr.as.transactionas.ASMXPT71001;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xpilottransactionmgr/transaction-logs")
public class ACMXPT71001 {

    private static final String AC = "ACMXPT71001";

    private final ASMXPT71001 asmxpt71001;

    public ACMXPT71001(ASMXPT71001 asmxpt71001) {
        this.asmxpt71001 = asmxpt71001;
    }

    @GetMapping("/{txLogId}")
    public StandardResponse<TransactionLogResponse> getLog(@PathVariable Long txLogId) {
        System.out.println("★★★★★★★ [" + AC + "] getLog START txLogId=" + txLogId);
        TransactionLogResponse response = asmxpt71001.getLog(txLogId);
        StandardResponse<TransactionLogResponse> result =
                StandardResponse.success("XPT-TX-DETAIL-001", "xpilotTransactionLogDetail", response);
        System.out.println("★★★★★★★ [" + AC + "] getLog END txLogId=" + txLogId);
        return result;
    }
}
