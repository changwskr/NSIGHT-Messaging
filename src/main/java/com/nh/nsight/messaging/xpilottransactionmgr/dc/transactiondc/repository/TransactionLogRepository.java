package com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.repository;

import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.XptTransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.dto.TransactionLogSearchDDTO;

import java.util.List;
import java.util.Optional;

public interface TransactionLogRepository {

    Optional<XptTransactionLog> findById(Long txLogId);

    List<XptTransactionLog> findLogs(TransactionLogSearchDDTO condition);

    long countLogs(TransactionLogSearchDDTO condition);

    List<XptTransactionLog> findLogsForDelete(TransactionLogSearchDDTO condition);

    int deleteById(Long txLogId);

    int deleteByCondition(TransactionLogSearchDDTO condition);
}
