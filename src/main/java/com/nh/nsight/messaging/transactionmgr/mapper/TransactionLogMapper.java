package com.nh.nsight.messaging.transactionmgr.mapper;

import com.nh.nsight.messaging.transactionmgr.dto.TransactionLogSearchCondition;
import com.nh.nsight.messaging.transactionmgr.thing.TransactionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TransactionLogMapper {
    int insertTransactionLog(TransactionLog log);
    TransactionLog selectById(@Param("txLogId") Long txLogId);
    List<TransactionLog> selectTransactionLogs(TransactionLogSearchCondition condition);
    long countTransactionLogs(TransactionLogSearchCondition condition);

    List<TransactionLog> selectTransactionLogsForDelete(TransactionLogSearchCondition condition);

    int deleteById(@Param("txLogId") Long txLogId);

    int deleteByCondition(TransactionLogSearchCondition condition);
}
