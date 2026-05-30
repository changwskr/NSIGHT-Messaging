package com.nh.nsight.messaging.transactionmgr.dao;

import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogSearchCondition;
import com.nh.nsight.messaging.transactionmgr.mapper.TransactionLogMapper;
import com.nh.nsight.messaging.transactionmgr.thing.TransactionLog;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionLogDao {
    private final TransactionLogMapper mapper;

    public TransactionLogDao(TransactionLogMapper mapper) {
        this.mapper = mapper;
    }

    public void insert(TransactionLog log) {
        mapper.insertTransactionLog(log);
    }

    public Optional<TransactionLog> findById(Long txLogId) {
        return Optional.ofNullable(mapper.selectById(txLogId));
    }

    public List<TransactionLog> findLogs(TransactionLogSearchCondition condition) {
        return mapper.selectTransactionLogs(condition);
    }

    public long countLogs(TransactionLogSearchCondition condition) {
        return mapper.countTransactionLogs(condition);
    }

    public List<TransactionLog> findLogsForDelete(TransactionLogSearchCondition condition) {
        return mapper.selectTransactionLogsForDelete(condition);
    }

    public int deleteById(Long txLogId) {
        return mapper.deleteById(txLogId);
    }

    public int deleteByCondition(TransactionLogSearchCondition condition) {
        return mapper.deleteByCondition(condition);
    }
}
