package com.nh.nsight.messaging.transactionmgr.controller;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogResponse;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogSearchCondition;
import com.nh.nsight.messaging.transactionmgr.facade.TransactionMgrFacade;
import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogDeleteResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction-logs")
public class TransactionMgrController {
    private final TransactionMgrFacade transactionMgrFacade;

    public TransactionMgrController(TransactionMgrFacade transactionMgrFacade) {
        this.transactionMgrFacade = transactionMgrFacade;
    }

    @GetMapping("/{txLogId}")
    public StandardResponse<TransactionLogResponse> getLog(@PathVariable Long txLogId) {
        TransactionLogResponse response = transactionMgrFacade.getLog(txLogId);
        return StandardResponse.success("TX-LOG-DETAIL-001", "transactionLogDetail", response);
    }

    @GetMapping
    public StandardResponse<List<TransactionLogResponse>> searchLogs(
            @RequestParam(required = false) String guid,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String serviceId,
            @RequestParam(required = false) String resultCode,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "3") int pageSize
    ) {
        TransactionLogSearchCondition condition = new TransactionLogSearchCondition(
                guid, traceId, transactionId, serviceId, resultCode, userId, pageNo, pageSize
        );
        List<TransactionLogResponse> rows = transactionMgrFacade.searchLogs(condition);
        long total = transactionMgrFacade.countLogs(condition);
        return StandardResponse.successPage("TX-LOG-LIST-001", "transactionLogList", rows,
                condition.getSafePageNo(), condition.getSafePageSize(), total);
    }

    @DeleteMapping("/{txLogId}")
    public StandardResponse<TransactionLogDeleteResponse> deleteLog(@PathVariable Long txLogId) {
        TransactionLogDeleteResponse result = transactionMgrFacade.deleteLog(txLogId);
        return StandardResponse.success("TX-LOG-DELETE-001", "transactionLogDelete", result);
    }

    @DeleteMapping
    public StandardResponse<TransactionLogDeleteResponse> deleteLogsByCondition(
            @RequestParam(required = false) String guid,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String serviceId,
            @RequestParam(required = false) String resultCode,
            @RequestParam(required = false) String userId
    ) {
        TransactionLogSearchCondition condition = new TransactionLogSearchCondition(
                guid, traceId, transactionId, serviceId, resultCode, userId, null, null
        );
        TransactionLogDeleteResponse result = transactionMgrFacade.deleteLogsByCondition(condition);
        return StandardResponse.success("TX-LOG-DELETE-002", "transactionLogBulkDelete", result);
    }
}
