package com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogDeleteResponse;
import com.nh.nsight.messaging.xpilottransactionmgr.as.transactionas.ASMXPT73001;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xpilottransactionmgr/transaction-logs")
public class ACMXPT73001 {

    private static final String AC = "ACMXPT73001";

    private final ASMXPT73001 asmxpt73001;

    public ACMXPT73001(ASMXPT73001 asmxpt73001) {
        this.asmxpt73001 = asmxpt73001;
    }

    @DeleteMapping("/{txLogId}")
    public StandardResponse<TransactionLogDeleteResponse> deleteLog(@PathVariable Long txLogId) {
        System.out.println("★★★★★★★ [" + AC + "] deleteLog START txLogId=" + txLogId);
        TransactionLogDeleteResponse response = asmxpt73001.deleteLog(txLogId);
        StandardResponse<TransactionLogDeleteResponse> result =
                StandardResponse.success("XPT-TX-DELETE-001", "xpilotTransactionLogDelete", response);
        System.out.println("★★★★★★★ [" + AC + "] deleteLog END txLogId=" + txLogId);
        return result;
    }
}
