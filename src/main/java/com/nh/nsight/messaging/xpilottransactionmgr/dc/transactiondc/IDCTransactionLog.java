package com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc;

import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.dto.TransactionLogSearchDDTO;

import java.util.List;

public interface IDCTransactionLog {

    XptTransactionLog getLog(Long txLogId);

    List<XptTransactionLog> searchLogs(TransactionLogSearchDDTO condition);

    long countLogs(TransactionLogSearchDDTO condition);

    List<XptTransactionLog> findLogsForDelete(TransactionLogSearchDDTO condition);

    int deleteById(Long txLogId);

    int deleteByCondition(TransactionLogSearchDDTO condition);
}
