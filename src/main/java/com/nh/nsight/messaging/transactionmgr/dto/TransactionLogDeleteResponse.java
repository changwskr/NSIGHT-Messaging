package com.nh.nsight.messaging.transactionmgr.dto;

import java.util.List;

public record TransactionLogDeleteResponse(
        int deletedLogCount,
        int deletedFileCount,
        List<String> deletedFilePaths
) {
}
