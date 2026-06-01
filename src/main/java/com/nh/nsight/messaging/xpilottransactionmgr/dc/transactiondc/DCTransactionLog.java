package com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.dto.TransactionLogSearchDDTO;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.repository.TransactionLogRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DCTransactionLog implements IDCTransactionLog {

    private static final String DC = "DCTransactionLog";

    private final TransactionLogRepository transactionLogRepository;

    public DCTransactionLog(TransactionLogRepository transactionLogRepository) {
        this.transactionLogRepository = transactionLogRepository;
    }

    @Override
    public XptTransactionLog getLog(Long txLogId) {
        System.out.println("★★★★★★★ [" + DC + "] getLog START txLogId=" + txLogId);
        XptTransactionLog result = transactionLogRepository.findById(txLogId).orElse(null);
        System.out.println("★★★★★★★ [" + DC + "] getLog END txLogId=" + txLogId);
        return result;
    }

    @Override
    public List<XptTransactionLog> searchLogs(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + DC + "] searchLogs START pageNo=" + condition.getSafePageNo());
        List<XptTransactionLog> result = transactionLogRepository.findLogs(condition);
        System.out.println("★★★★★★★ [" + DC + "] searchLogs END size=" + result.size());
        return result;
    }

    @Override
    public long countLogs(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + DC + "] countLogs START");
        long total = transactionLogRepository.countLogs(condition);
        System.out.println("★★★★★★★ [" + DC + "] countLogs END total=" + total);
        return total;
    }

    @Override
    public List<XptTransactionLog> findLogsForDelete(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + DC + "] findLogsForDelete START");
        List<XptTransactionLog> result = transactionLogRepository.findLogsForDelete(condition);
        System.out.println("★★★★★★★ [" + DC + "] findLogsForDelete END size=" + result.size());
        return result;
    }

    @Override
    public int deleteById(Long txLogId) {
        System.out.println("★★★★★★★ [" + DC + "] deleteById START txLogId=" + txLogId);
        int deleted = transactionLogRepository.deleteById(txLogId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "txLogId=" + txLogId);
        }
        System.out.println("★★★★★★★ [" + DC + "] deleteById END txLogId=" + txLogId);
        return deleted;
    }

    @Override
    public int deleteByCondition(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + DC + "] deleteByCondition START");
        int deleted = transactionLogRepository.deleteByCondition(condition);
        System.out.println("★★★★★★★ [" + DC + "] deleteByCondition END deleted=" + deleted);
        return deleted;
    }
}
