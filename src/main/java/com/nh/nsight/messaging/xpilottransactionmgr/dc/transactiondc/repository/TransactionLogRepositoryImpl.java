package com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.repository;

import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.XptTransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.dto.TransactionLogSearchDDTO;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.mapper.XptTransactionLogMapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionLogRepositoryImpl implements TransactionLogRepository {

    private static final String REPO = "TransactionLogRepositoryImpl";

    private final XptTransactionLogMapper xptTransactionLogMapper;

    public TransactionLogRepositoryImpl(XptTransactionLogMapper xptTransactionLogMapper) {
        this.xptTransactionLogMapper = xptTransactionLogMapper;
    }

    @Override
    public Optional<XptTransactionLog> findById(Long txLogId) {
        System.out.println("★★★★★★★ [" + REPO + "] findById START txLogId=" + txLogId);
        Optional<XptTransactionLog> result = Optional.ofNullable(xptTransactionLogMapper.selectById(txLogId));
        System.out.println("★★★★★★★ [" + REPO + "] findById END txLogId=" + txLogId
                + " present=" + result.isPresent());
        return result;
    }

    @Override
    public List<XptTransactionLog> findLogs(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + REPO + "] findLogs START pageNo=" + condition.getSafePageNo());
        List<XptTransactionLog> result = xptTransactionLogMapper.selectTransactionLogs(condition);
        System.out.println("★★★★★★★ [" + REPO + "] findLogs END size=" + result.size());
        return result;
    }

    @Override
    public long countLogs(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + REPO + "] countLogs START");
        long total = xptTransactionLogMapper.countTransactionLogs(condition);
        System.out.println("★★★★★★★ [" + REPO + "] countLogs END total=" + total);
        return total;
    }

    @Override
    public List<XptTransactionLog> findLogsForDelete(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + REPO + "] findLogsForDelete START");
        List<XptTransactionLog> result = xptTransactionLogMapper.selectTransactionLogsForDelete(condition);
        System.out.println("★★★★★★★ [" + REPO + "] findLogsForDelete END size=" + result.size());
        return result;
    }

    @Override
    public int deleteById(Long txLogId) {
        System.out.println("★★★★★★★ [" + REPO + "] deleteById START txLogId=" + txLogId);
        int deleted = xptTransactionLogMapper.deleteById(txLogId);
        System.out.println("★★★★★★★ [" + REPO + "] deleteById END txLogId=" + txLogId + " deleted=" + deleted);
        return deleted;
    }

    @Override
    public int deleteByCondition(TransactionLogSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + REPO + "] deleteByCondition START");
        int deleted = xptTransactionLogMapper.deleteByCondition(condition);
        System.out.println("★★★★★★★ [" + REPO + "] deleteByCondition END deleted=" + deleted);
        return deleted;
    }
}
