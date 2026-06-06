package com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.mapper;

import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwAuditLog;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxLog;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxStatus;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.dto.FwLogSearchDDTO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 프레임워크 MyBatis 매퍼 인터페이스 (DC 계층).
 * <p>
 * fw_tx_status, fw_tx_log, fw_audit_log 테이블에 대한 SQL 매핑을 정의한다.
 * Repository 구현체가 이 매퍼를 통해 실제 DB 작업을 수행한다.
 */
@Mapper
public interface FwFrameworkMapper {

    /** 거래 상태 INSERT */
    int insertTxStatus(FwTxStatus row);

    /** 거래 상태 UPDATE */
    int updateTxStatus(FwTxStatus row);

    /** GUID로 거래 상태 단건 조회 */
    FwTxStatus selectTxStatusByGuid(@Param("guid") String guid);

    /** 멱등성 키로 거래 상태 단건 조회 */
    FwTxStatus selectTxStatusByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey);

    /** GUID·거래ID·멱등성 키 복합 조건으로 거래 상태 조회 */
    FwTxStatus selectTxStatusByKeys(@Param("guid") String guid,
            @Param("transactionId") String transactionId,
            @Param("idempotencyKey") String idempotencyKey);

    /** 거래 로그 INSERT */
    int insertTxLog(FwTxLog row);

    /** 로그 ID로 거래 로그 단건 조회 */
    FwTxLog selectTxLogById(@Param("logId") Long logId);

    /** 검색 조건으로 거래 로그 목록 조회 (페이징) */
    List<FwTxLog> selectTxLogs(FwLogSearchDDTO criteria);

    /** 검색 조건으로 거래 로그 전체 건수 조회 */
    long countTxLogs(FwLogSearchDDTO criteria);

    /** 로그 ID로 거래 로그 DELETE */
    int deleteTxLogById(@Param("logId") Long logId);

    /** 감사 로그 INSERT */
    int insertAuditLog(FwAuditLog row);

    /** 검색 조건으로 감사 로그 목록 조회 (페이징) */
    List<FwAuditLog> selectAuditLogs(FwLogSearchDDTO criteria);

    /** 검색 조건으로 감사 로그 전체 건수 조회 */
    long countAuditLogs(FwLogSearchDDTO criteria);
}
