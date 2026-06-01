package com.nh.nsight.messaging.xpilottransactionmgr.as.transactionas;

import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogResponse;
import com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac.dto.TransactionLogCDtoConverter;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.DCTransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.dto.TransactionLogSearchDDTO;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ASMXPT72001 {

    private static final String AS = "ASMXPT72001";

    private final DCTransactionLog dcTransactionLog;

    public ASMXPT72001(DCTransactionLog dcTransactionLog) {
        this.dcTransactionLog = dcTransactionLog;
    }

    public List<TransactionLogResponse> searchLogs(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + AS + "] searchLogs START pageNo=" + condition.getSafePageNo());
        List<TransactionLogResponse> result =
                TransactionLogCDtoConverter.toResponseList(dcTransactionLog.searchLogs(condition));
        System.out.println("★★★★★★★ [" + AS + "] searchLogs END size=" + result.size());
        return result;
    }

    public long countLogs(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + AS + "] countLogs START");
        long total = dcTransactionLog.countLogs(condition);
        System.out.println("★★★★★★★ [" + AS + "] countLogs END total=" + total);
        return total;
    }
}
