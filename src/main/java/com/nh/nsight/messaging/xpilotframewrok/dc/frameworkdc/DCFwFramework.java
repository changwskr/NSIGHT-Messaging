package com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc;

import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.dto.FwLogSearchDDTO;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.repository.FwFrameworkRepository;
import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.common.exception.FwFrameworkException;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 프레임워크 DC(Data Component) 구현체.
 * <p>
 * {@link FwProcessContext}를 엔티티로 변환하여 Repository를 통해 DB에 영속화한다.
 * PRE-010(멱등성 조회), PRE-012/014(거래 시작 INSERT), POST-006~009(후처리 UPDATE/INSERT)에서 호출된다.
 * 설계서 4장(전처리)·5장(후처리) DC 계층 명세를 따른다.
 */
@Repository
public class DCFwFramework implements IDCFwFramework {

    private static final String DC = "DCFwFramework";

    /** 프레임워크 Repository (MyBatis 위임) */
    private final FwFrameworkRepository repository;

    public DCFwFramework(FwFrameworkRepository repository) {
        this.repository = repository;
    }

    /**
     * 거래 상태를 신규 등록한다 (PRE-012, PRE-014).
     * 컨텍스트를 엔티티로 변환 후 INSERT하고, 생성된 txStatusId를 attributes에 저장한다.
     *
     * @param context 전처리·후처리 공유 컨텍스트
     */
    @Override
    public void saveTxStatus(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + DC + "] saveTxStatus START guid=" + context.getGuid());
        FwTxStatus row = toTxStatus(context); // 컨텍스트 → 엔티티 변환
        row.setRetryYn("N");
        row.setRetryCount(0);
        repository.insertTxStatus(row); // DB INSERT
        context.getAttributes().put("txStatusId", row.getTxStatusId()); // 후처리 UPDATE용 PK 보관
        System.out.println("★★★★★★★ [" + DC + "] saveTxStatus END txStatusId=" + row.getTxStatusId());
    }

    /**
     * 거래 상태를 갱신한다 (POST-009).
     * attributes에 txStatusId가 없으면 GUID로 기존 레코드를 조회하여 보완한다.
     *
     * @param context 전처리·후처리 공유 컨텍스트
     */
    @Override
    public void updateTxStatus(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + DC + "] updateTxStatus START guid=" + context.getGuid());
        Object id = context.getAttributes().get("txStatusId");
        if (id == null) {
            // PRE 단계에서 txStatusId가 누락된 경우 GUID로 기존 레코드 조회
            FwTxStatus found = repository.findTxStatusByGuid(context.getGuid());
            if (found == null) {
                System.out.println("★★★★★★★ [" + DC + "] updateTxStatus END skipped=notFound");
                return;
            }
            context.getAttributes().put("txStatusId", found.getTxStatusId());
        }
        FwTxStatus row = toTxStatus(context);
        row.setTxStatusId((Long) context.getAttributes().get("txStatusId"));
        repository.updateTxStatus(row); // DB UPDATE
        System.out.println("★★★★★★★ [" + DC + "] updateTxStatus END txStatusId=" + row.getTxStatusId());
    }

    /**
     * GUID·거래ID·멱등성 키로 거래 상태를 조회한다 (PRE-010).
     *
     * @param guid            글로벌 요청 추적 ID
     * @param transactionId   거래 ID
     * @param idempotencyKey  멱등성 키
     * @return 조회된 거래 상태, 없으면 null
     */
    @Override
    public FwTxStatus getTxStatus(String guid, String transactionId, String idempotencyKey) {
        System.out.println("★★★★★★★ [" + DC + "] getTxStatus START transactionId=" + transactionId);
        FwTxStatus result = repository.findTxStatusByKeys(guid, transactionId, idempotencyKey); // DB SELECT
        System.out.println("★★★★★★★ [" + DC + "] getTxStatus END found=" + (result != null));
        return result;
    }

    /**
     * 거래 성능 로그를 저장한다 (POST-006, POST-007).
     *
     * @param context 전처리·후처리 공유 컨텍스트
     */
    @Override
    public void saveTxLog(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + DC + "] saveTxLog START guid=" + context.getGuid());
        FwTxLog row = new FwTxLog();
        row.setGuid(context.getGuid());
        row.setTraceId(context.getTraceId());
        row.setServiceId(context.getServiceId());
        row.setUserId(context.getUserId());
        row.setRequestUri(context.getRequestUri());
        row.setHttpMethod(context.getHttpMethod());
        row.setResultCode(context.getResultCode());
        row.setErrorCode(context.getErrorCode());
        row.setApId(context.getApId());
        row.setDbTime(context.getDbTimeMs());
        row.setExtTime(context.getExtTimeMs());
        row.setTotalTime(context.getTotalTimeMs());
        repository.insertTxLog(row); // DB INSERT
        System.out.println("★★★★★★★ [" + DC + "] saveTxLog END");
    }

    /**
     * 거래 로그 단건을 조회한다.
     *
     * @param logId 로그 ID
     * @return 조회된 거래 로그, 없으면 null
     */
    @Override
    public FwTxLog getTxLog(Long logId) {
        System.out.println("★★★★★★★ [" + DC + "] getTxLog START logId=" + logId);
        FwTxLog result = repository.findTxLogById(logId); // DB SELECT
        System.out.println("★★★★★★★ [" + DC + "] getTxLog END logId=" + logId
                + " found=" + (result != null));
        return result;
    }

    /**
     * 거래 로그 목록을 검색 조건으로 조회한다.
     *
     * @param criteria 검색·페이징 조건
     * @return 거래 로그 목록
     */
    @Override
    public List<FwTxLog> listTxLogs(FwLogSearchDDTO criteria) {
        System.out.println("★★★★★★★ [" + DC + "] listTxLogs START pageNo=" + criteria.getSafePageNo());
        List<FwTxLog> result = repository.findTxLogs(criteria); // DB SELECT 목록
        System.out.println("★★★★★★★ [" + DC + "] listTxLogs END size=" + result.size());
        return result;
    }

    /**
     * 거래 로그 전체 건수를 검색 조건으로 조회한다.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    @Override
    public long countTxLogs(FwLogSearchDDTO criteria) {
        System.out.println("★★★★★★★ [" + DC + "] countTxLogs START");
        long total = repository.countTxLogs(criteria); // DB COUNT
        System.out.println("★★★★★★★ [" + DC + "] countTxLogs END total=" + total);
        return total;
    }

    /**
     * 거래 로그 단건을 삭제한다. 삭제 대상이 없으면 BIZ-001 예외를 발생시킨다.
     *
     * @param logId 삭제 대상 로그 ID
     */
    @Override
    public void deleteTxLog(Long logId) {
        System.out.println("★★★★★★★ [" + DC + "] deleteTxLog START logId=" + logId);
        if (repository.deleteTxLogById(logId) == 0) { // DB DELETE, 0건이면 예외
            throw new FwFrameworkException("BIZ-001", "logId=" + logId);
        }
        System.out.println("★★★★★★★ [" + DC + "] deleteTxLog END logId=" + logId);
    }

    /**
     * 감사 로그를 저장한다 (POST-008).
     * actionType이 없으면 감사 로그 적재를 건너뛴다.
     *
     * @param context 전처리·후처리 공유 컨텍스트
     */
    @Override
    public void saveAuditLog(FwProcessContext context) {
        System.out.println("★★★★★★★ [" + DC + "] saveAuditLog START guid=" + context.getGuid());
        if (context.getActionType() == null || context.getActionType().isBlank()) {
            System.out.println("★★★★★★★ [" + DC + "] saveAuditLog END skipped=noActionType");
            return; // 감사 대상 행위가 없으면 INSERT 생략
        }
        FwAuditLog row = new FwAuditLog();
        row.setGuid(context.getGuid());
        row.setUserId(context.getUserId());
        row.setBranchId(context.getBranchId());
        row.setMenuId(context.getMenuId());
        row.setFunctionId(context.getFunctionId());
        row.setCustomerId(context.getCustomerId());
        row.setActionType(context.getActionType());
        row.setAccessPurpose(context.getAccessPurpose());
        row.setMaskingLevel(context.getMaskingLevel());
        row.setResultCode(context.getResultCode());
        row.setClientIp(context.getClientIp());
        repository.insertAuditLog(row); // DB INSERT
        System.out.println("★★★★★★★ [" + DC + "] saveAuditLog END");
    }

    /**
     * 감사 로그 목록을 검색 조건으로 조회한다.
     *
     * @param criteria 검색·페이징 조건
     * @return 감사 로그 목록
     */
    @Override
    public List<FwAuditLog> listAuditLogs(FwLogSearchDDTO criteria) {
        System.out.println("★★★★★★★ [" + DC + "] listAuditLogs START pageNo=" + criteria.getSafePageNo());
        List<FwAuditLog> result = repository.findAuditLogs(criteria); // DB SELECT 목록
        System.out.println("★★★★★★★ [" + DC + "] listAuditLogs END size=" + result.size());
        return result;
    }

    /**
     * 감사 로그 전체 건수를 검색 조건으로 조회한다.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    @Override
    public long countAuditLogs(FwLogSearchDDTO criteria) {
        System.out.println("★★★★★★★ [" + DC + "] countAuditLogs START");
        long total = repository.countAuditLogs(criteria); // DB COUNT
        System.out.println("★★★★★★★ [" + DC + "] countAuditLogs END total=" + total);
        return total;
    }

    /**
     * FwProcessContext를 FwTxStatus 엔티티로 변환한다.
     *
     * @param context 전처리·후처리 공유 컨텍스트
     * @return 변환된 거래 상태 엔티티
     */
    private static FwTxStatus toTxStatus(FwProcessContext context) {
        FwTxStatus row = new FwTxStatus();
        row.setGuid(context.getGuid());
        row.setTraceId(context.getTraceId());
        row.setTransactionId(context.getTransactionId());
        row.setServiceId(context.getServiceId());
        row.setUserId(context.getUserId());
        row.setBranchId(context.getBranchId());
        row.setChannelId(context.getChannelId());
        row.setStatus(context.getStatus().name());
        row.setResultCode(context.getResultCode());
        row.setErrorCode(context.getErrorCode());
        row.setRequestTime(context.getRequestTime());
        row.setStartTime(context.getStartTime());
        row.setEndTime(context.getEndTime());
        row.setElapsedTime(context.getTotalTimeMs());
        row.setIdempotencyKey(context.getIdempotencyKey());
        return row;
    }
}
