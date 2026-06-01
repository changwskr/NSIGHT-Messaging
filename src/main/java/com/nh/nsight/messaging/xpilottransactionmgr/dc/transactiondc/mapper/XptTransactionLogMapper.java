package com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.mapper;

import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.XptTransactionLog;
import com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.dto.TransactionLogSearchDDTO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XptTransactionLogMapper {

    XptTransactionLog selectById(@Param("txLogId") Long txLogId);

    List<XptTransactionLog> selectTransactionLogs(TransactionLogSearchDDTO condition);

    long countTransactionLogs(TransactionLogSearchDDTO condition);

    List<XptTransactionLog> selectTransactionLogsForDelete(TransactionLogSearchDDTO condition);

    int deleteById(@Param("txLogId") Long txLogId);

    int deleteByCondition(TransactionLogSearchDDTO condition);
}
