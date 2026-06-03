package com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac.dto;

import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogResponse;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.XptTransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.dto.TransactionLogSearchDDTO;
import com.nh.nsight.messaging.xpilottransactionmgr.util.TransactionLogMapperUtil;

import java.util.List;

public final class TransactionLogCDtoConverter {

    private TransactionLogCDtoConverter() {
    }

    public static TransactionLogResponse toResponse(XptTransactionLog log) {
        return TransactionLogResponse.from(TransactionLogMapperUtil.toLegacyLog(log));
    }

    public static List<TransactionLogResponse> toResponseList(List<XptTransactionLog> logs) {
        return logs.stream().map(TransactionLogCDtoConverter::toResponse).toList();
    }

    public static TransactionLogSearchDDTO toSearchDDTO(String guid, String traceId, String transactionId,
                                                        String serviceId, String resultCode, String userId,
                                                        Integer pageNo, Integer pageSize) {
        TransactionLogSearchDDTO dto = new TransactionLogSearchDDTO();
        dto.setGuid(guid);
        dto.setTraceId(traceId);
        dto.setTransactionId(transactionId);
        dto.setServiceId(serviceId);
        dto.setResultCode(resultCode);
        dto.setUserId(userId);
        dto.setPageNo(pageNo);
        dto.setPageSize(pageSize);
        return dto;
    }
}
