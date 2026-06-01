package com.nh.nsight.messaging.xpilottransactionmgr.as.transactionas;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.common.log.EnvelopeFileDeleteResult;
import com.nh.nsight.messaging.common.log.MessageEnvelopeFileService;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogDeleteResponse;
import com.nh.nsight.messaging.transactionmgr.thing.TransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.DCTransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.XptTransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.zcommonutil.TransactionLogMapperUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ASMXPT73001 {

    private static final String AS = "ASMXPT73001";

    private final DCTransactionLog dcTransactionLog;
    private final MessageEnvelopeFileService messageEnvelopeFileService;

    public ASMXPT73001(DCTransactionLog dcTransactionLog,
                       MessageEnvelopeFileService messageEnvelopeFileService) {
        this.dcTransactionLog = dcTransactionLog;
        this.messageEnvelopeFileService = messageEnvelopeFileService;
    }

    @Transactional(timeout = 5)
    public TransactionLogDeleteResponse deleteLog(Long txLogId) {
        System.out.println("★★★★★★★ [" + AS + "] deleteLog START txLogId=" + txLogId);
        XptTransactionLog xpt = dcTransactionLog.getLog(txLogId);
        if (xpt == null) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "txLogId=" + txLogId);
        }
        TransactionLog legacy = TransactionLogMapperUtil.toLegacyLog(xpt);
        EnvelopeFileDeleteResult fileResult = messageEnvelopeFileService.deleteForTransactionLog(legacy);
        dcTransactionLog.deleteById(txLogId);
        TransactionLogDeleteResponse result = new TransactionLogDeleteResponse(
                1, fileResult.deletedFileCount(), fileResult.deletedFilePaths());
        System.out.println("★★★★★★★ [" + AS + "] deleteLog END txLogId=" + txLogId);
        return result;
    }
}
