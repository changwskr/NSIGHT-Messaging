package com.nh.nsight.messaging.transactionmgr.facade;

import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogDeleteResponse;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogResponse;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogSearchCondition;
import com.nh.nsight.messaging.transactionmgr.service.TransactionLogService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TransactionMgrFacade {
    private final TransactionLogService transactionLogService;

    public TransactionMgrFacade(TransactionLogService transactionLogService) {
        this.transactionLogService = transactionLogService;
    }

    @Transactional(readOnly = true, timeout = 3)
    public TransactionLogResponse getLog(Long txLogId) {
        return transactionLogService.getLog(txLogId);
    }

    @Transactional(readOnly = true, timeout = 3)
    public List<TransactionLogResponse> searchLogs(TransactionLogSearchCondition condition) {
        return transactionLogService.searchLogs(condition);
    }

    @Transactional(readOnly = true, timeout = 3)
    public long countLogs(TransactionLogSearchCondition condition) {
        return transactionLogService.countLogs(condition);
    }

    @Transactional(timeout = 5)
    public TransactionLogDeleteResponse deleteLog(Long txLogId) {
        return transactionLogService.deleteLog(txLogId);
    }

    @Transactional(timeout = 10)
    public TransactionLogDeleteResponse deleteLogsByCondition(TransactionLogSearchCondition condition) {
        return transactionLogService.deleteLogsByCondition(condition);
    }
}
