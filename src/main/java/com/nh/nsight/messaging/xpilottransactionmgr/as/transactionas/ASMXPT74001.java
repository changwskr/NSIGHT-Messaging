package com.nh.nsight.messaging.xpilottransactionmgr.as.transactionas;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.common.log.EnvelopeFileDeleteResult;
import com.nh.nsight.messaging.common.log.MessageEnvelopeFileService;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogDeleteResponse;
import com.nh.nsight.messaging.transactionmgr.thing.TransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.DCTransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.XptTransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.dto.TransactionLogSearchDDTO;
import com.nh.nsight.messaging.xpilottransactionmgr.zcommonutil.TransactionLogMapperUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ASMXPT74001 {

    private static final int MAX_BULK_DELETE = 500;
    private static final String AS = "ASMXPT74001";

    private final DCTransactionLog dcTransactionLog;
    private final MessageEnvelopeFileService messageEnvelopeFileService;

    public ASMXPT74001(DCTransactionLog dcTransactionLog,
                       MessageEnvelopeFileService messageEnvelopeFileService) {
        this.dcTransactionLog = dcTransactionLog;
        this.messageEnvelopeFileService = messageEnvelopeFileService;
    }

    @Transactional(timeout = 10)
    public TransactionLogDeleteResponse deleteLogsByCondition(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + AS + "] deleteLogsByCondition START");
        List<XptTransactionLog> targets = dcTransactionLog.findLogsForDelete(condition);
        if (targets.isEmpty()) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "삭제할 트랜잭션 로그가 없습니다.");
        }
        if (targets.size() > MAX_BULK_DELETE) {
            throw new BusinessException(ErrorCode.VAL_INVALID_REQUEST,
                    "한 번에 삭제 가능한 건수는 " + MAX_BULK_DELETE + "건입니다. 조회 조건을 좁혀 주세요.");
        }

        Set<String> deletedFilePaths = new LinkedHashSet<>();
        int deletedFileCount = 0;
        for (XptTransactionLog xpt : targets) {
            TransactionLog legacy = TransactionLogMapperUtil.toLegacyLog(xpt);
            EnvelopeFileDeleteResult fileResult = messageEnvelopeFileService.deleteForTransactionLog(legacy);
            deletedFileCount += fileResult.deletedFileCount();
            deletedFilePaths.addAll(fileResult.deletedFilePaths());
        }
        int deletedLogCount = dcTransactionLog.deleteByCondition(condition);
        TransactionLogDeleteResponse result = new TransactionLogDeleteResponse(
                deletedLogCount,
                deletedFileCount,
                List.copyOf(deletedFilePaths)
        );
        System.out.println("★★★★★★★ [" + AS + "] deleteLogsByCondition END deletedLogCount=" + deletedLogCount);
        return result;
    }
}
