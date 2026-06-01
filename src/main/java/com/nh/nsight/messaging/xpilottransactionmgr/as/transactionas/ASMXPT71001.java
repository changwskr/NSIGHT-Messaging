package com.nh.nsight.messaging.xpilottransactionmgr.as.transactionas;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogResponse;
import com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac.dto.TransactionLogCDtoConverter;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.DCTransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.XptTransactionLog;

import org.springframework.stereotype.Service;

@Service
public class ASMXPT71001 {

    private static final String AS = "ASMXPT71001";

    private final DCTransactionLog dcTransactionLog;

    public ASMXPT71001(DCTransactionLog dcTransactionLog) {
        this.dcTransactionLog = dcTransactionLog;
    }

    public TransactionLogResponse getLog(Long txLogId) {
        System.out.println("★★★★★★★ [" + AS + "] getLog START txLogId=" + txLogId);
        XptTransactionLog log = dcTransactionLog.getLog(txLogId);
        if (log == null) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "txLogId=" + txLogId);
        }
        TransactionLogResponse result = TransactionLogCDtoConverter.toResponse(log);
        System.out.println("★★★★★★★ [" + AS + "] getLog END txLogId=" + txLogId);
        return result;
    }
}
