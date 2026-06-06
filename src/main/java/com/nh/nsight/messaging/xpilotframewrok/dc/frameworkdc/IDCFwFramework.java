package com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc;

import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.dto.FwLogSearchDDTO;
import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;

import java.util.List;

/**
 * 프레임워크 DC(Data Component) 계층 인터페이스.
 * <p>
 * 거래 상태·거래 로그·감사 로그에 대한 영속성 작업을 정의한다.
 * PRE-010(멱등성), PRE-012/014(거래 시작), POST-006~009(후처리)에서 AS 계층이 호출한다.
 */
public interface IDCFwFramework {

    /**
     * 거래 상태를 신규 등록한다 (PRE-012, PRE-014).
     *
     * @param context 전처리·후처리 공유 컨텍스트
     */
    void saveTxStatus(FwProcessContext context);

    /**
     * 거래 상태를 갱신한다 (POST-009).
     *
     * @param context 전처리·후처리 공유 컨텍스트
     */
    void updateTxStatus(FwProcessContext context);

    /**
     * GUID·거래ID·멱등성 키로 거래 상태를 조회한다 (PRE-010).
     *
     * @param guid            글로벌 요청 추적 ID
     * @param transactionId   거래 ID
     * @param idempotencyKey  멱등성 키
     * @return 조회된 거래 상태, 없으면 null
     */
    FwTxStatus getTxStatus(String guid, String transactionId, String idempotencyKey);

    /**
     * 거래 성능 로그를 저장한다 (POST-006, POST-007).
     *
     * @param context 전처리·후처리 공유 컨텍스트
     */
    void saveTxLog(FwProcessContext context);

    /**
     * 거래 로그 단건을 조회한다.
     *
     * @param logId 로그 ID
     * @return 조회된 거래 로그, 없으면 null
     */
    FwTxLog getTxLog(Long logId);

    /**
     * 거래 로그 목록을 검색 조건으로 조회한다.
     *
     * @param criteria 검색·페이징 조건
     * @return 거래 로그 목록
     */
    List<FwTxLog> listTxLogs(FwLogSearchDDTO criteria);

    /**
     * 거래 로그 전체 건수를 검색 조건으로 조회한다.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    long countTxLogs(FwLogSearchDDTO criteria);

    /**
     * 거래 로그 단건을 삭제한다.
     *
     * @param logId 삭제 대상 로그 ID
     */
    void deleteTxLog(Long logId);

    /**
     * 감사 로그를 저장한다 (POST-008).
     *
     * @param context 전처리·후처리 공유 컨텍스트
     */
    void saveAuditLog(FwProcessContext context);

    /**
     * 감사 로그 목록을 검색 조건으로 조회한다.
     *
     * @param criteria 검색·페이징 조건
     * @return 감사 로그 목록
     */
    List<FwAuditLog> listAuditLogs(FwLogSearchDDTO criteria);

    /**
     * 감사 로그 전체 건수를 검색 조건으로 조회한다.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    long countAuditLogs(FwLogSearchDDTO criteria);
}
