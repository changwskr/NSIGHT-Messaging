package com.nh.nsight.messaging.transactionmgr.service;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.common.log.EnvelopeFileDeleteResult;
import com.nh.nsight.messaging.common.log.MessageEnvelopeFileService;
import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.transactionmgr.dao.TransactionLogDao;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogDeleteResponse;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogResponse;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogSearchCondition;
import com.nh.nsight.messaging.transactionmgr.thing.TransactionLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransactionLogService {

    private static final int MAX_BULK_DELETE = 500;

    private final TransactionLogDao transactionLogDao;
    private final MessageEnvelopeFileService messageEnvelopeFileService;

    public TransactionLogService(TransactionLogDao transactionLogDao,
                                 MessageEnvelopeFileService messageEnvelopeFileService) {
        this.transactionLogDao = transactionLogDao;
        this.messageEnvelopeFileService = messageEnvelopeFileService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 3)
    public void record(StandardResponse<?> response, String requestUri, String httpMethod) {
        TransactionLog log = TransactionLog.from(response, requestUri, httpMethod);
        transactionLogDao.insert(log);
    }

    public TransactionLogResponse getLog(Long txLogId) {
        TransactionLog log = transactionLogDao.findById(txLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BIZ_NO_DATA, "txLogId=" + txLogId));
        return TransactionLogResponse.from(log);
    }

    public List<TransactionLogResponse> searchLogs(TransactionLogSearchCondition condition) {
        return transactionLogDao.findLogs(condition).stream()
                .map(TransactionLogResponse::from)
                .toList();
    }

    public long countLogs(TransactionLogSearchCondition condition) {
        return transactionLogDao.countLogs(condition);
    }

    @Transactional(timeout = 5)
    public TransactionLogDeleteResponse deleteLog(Long txLogId) {
        TransactionLog log = transactionLogDao.findById(txLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BIZ_NO_DATA, "txLogId=" + txLogId));
        EnvelopeFileDeleteResult fileResult = messageEnvelopeFileService.deleteForTransactionLog(log);
        int deleted = transactionLogDao.deleteById(txLogId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "txLogId=" + txLogId);
        }
        return new TransactionLogDeleteResponse(1, fileResult.deletedFileCount(), fileResult.deletedFilePaths());
    }

    @Transactional(timeout = 10)
    public TransactionLogDeleteResponse deleteLogsByCondition(TransactionLogSearchCondition condition) {
        List<TransactionLog> targets = transactionLogDao.findLogsForDelete(condition);
        if (targets.isEmpty()) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "삭제할 트랜잭션 로그가 없습니다.");
        }
        if (targets.size() > MAX_BULK_DELETE) {
            throw new BusinessException(ErrorCode.VAL_INVALID_REQUEST,
                    "한 번에 삭제 가능한 건수는 " + MAX_BULK_DELETE + "건입니다. 조회 조건을 좁혀 주세요.");
        }

        Set<String> deletedFilePaths = new LinkedHashSet<>();
        int deletedFileCount = 0;
        for (TransactionLog log : targets) {
            EnvelopeFileDeleteResult fileResult = messageEnvelopeFileService.deleteForTransactionLog(log);
            deletedFileCount += fileResult.deletedFileCount();
            deletedFilePaths.addAll(fileResult.deletedFilePaths());
        }
        int deletedLogCount = transactionLogDao.deleteByCondition(condition);
        return new TransactionLogDeleteResponse(
                deletedLogCount,
                deletedFileCount,
                List.copyOf(deletedFilePaths)
        );
    }
}
