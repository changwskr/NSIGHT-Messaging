package com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.repository;

import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwAuditLog;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxLog;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxStatus;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.dto.FwLogSearchDDTO;

import java.util.List;

/**
 * 프레임워크 Repository 인터페이스 (DC 계층).
 * <p>
 * MyBatis 매퍼를 추상화하여 DC 구현체({@link com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.DCFwFramework})가
 * DB 작업을 도메인 용어로 호출할 수 있게 한다.
 */
public interface FwFrameworkRepository {

    /** 거래 상태 INSERT */
    void insertTxStatus(FwTxStatus row);

    /** 거래 상태 UPDATE */
    void updateTxStatus(FwTxStatus row);

    /** GUID로 거래 상태 단건 조회 */
    FwTxStatus findTxStatusByGuid(String guid);

    /** 멱등성 키로 거래 상태 단건 조회 */
    FwTxStatus findTxStatusByIdempotencyKey(String idempotencyKey);

    /** GUID·거래ID·멱등성 키 복합 조건으로 거래 상태 조회 */
    FwTxStatus findTxStatusByKeys(String guid, String transactionId, String idempotencyKey);

    /** 거래 로그 INSERT */
    void insertTxLog(FwTxLog row);

    /** 로그 ID로 거래 로그 단건 조회 */
    FwTxLog findTxLogById(Long logId);

    /** 검색 조건으로 거래 로그 목록 조회 */
    List<FwTxLog> findTxLogs(FwLogSearchDDTO criteria);

    /** 검색 조건으로 거래 로그 전체 건수 조회 */
    long countTxLogs(FwLogSearchDDTO criteria);

    /** 로그 ID로 거래 로그 DELETE, 삭제된 행 수 반환 */
    int deleteTxLogById(Long logId);

    /** 감사 로그 INSERT */
    void insertAuditLog(FwAuditLog row);

    /** 검색 조건으로 감사 로그 목록 조회 */
    List<FwAuditLog> findAuditLogs(FwLogSearchDDTO criteria);

    /** 검색 조건으로 감사 로그 전체 건수 조회 */
    long countAuditLogs(FwLogSearchDDTO criteria);
}
