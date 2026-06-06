package com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.repository;

import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwAuditLog;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxLog;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxStatus;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.dto.FwLogSearchDDTO;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.mapper.FwFrameworkMapper;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 프레임워크 Repository 구현체 (DC 계층).
 * <p>
 * {@link FwFrameworkMapper}를 위임하여 fw_tx_status, fw_tx_log, fw_audit_log 테이블에
 * 대한 CRUD 작업을 수행한다. DC 구현체와 매퍼 사이의 중간 계층 역할을 한다.
 */
@Repository
public class FwFrameworkRepositoryImpl implements FwFrameworkRepository {

    private static final String REPO = "FwFrameworkRepositoryImpl";

    /** MyBatis SQL 매퍼 */
    private final FwFrameworkMapper mapper;

    public FwFrameworkRepositoryImpl(FwFrameworkMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void insertTxStatus(FwTxStatus row) {
        System.out.println("★★★★★★★ [" + REPO + "] insertTxStatus START transactionId=" + row.getTransactionId());
        mapper.insertTxStatus(row); // DB INSERT: fw_tx_status
        System.out.println("★★★★★★★ [" + REPO + "] insertTxStatus END txStatusId=" + row.getTxStatusId());
    }

    @Override
    public void updateTxStatus(FwTxStatus row) {
        System.out.println("★★★★★★★ [" + REPO + "] updateTxStatus START txStatusId=" + row.getTxStatusId());
        mapper.updateTxStatus(row); // DB UPDATE: fw_tx_status
        System.out.println("★★★★★★★ [" + REPO + "] updateTxStatus END txStatusId=" + row.getTxStatusId());
    }

    @Override
    public FwTxStatus findTxStatusByGuid(String guid) {
        System.out.println("★★★★★★★ [" + REPO + "] findTxStatusByGuid START guid=" + guid);
        FwTxStatus result = mapper.selectTxStatusByGuid(guid); // DB SELECT by GUID
        System.out.println("★★★★★★★ [" + REPO + "] findTxStatusByGuid END found=" + (result != null));
        return result;
    }

    @Override
    public FwTxStatus findTxStatusByIdempotencyKey(String idempotencyKey) {
        System.out.println("★★★★★★★ [" + REPO + "] findTxStatusByIdempotencyKey START");
        FwTxStatus result = mapper.selectTxStatusByIdempotencyKey(idempotencyKey); // DB SELECT by 멱등성 키
        System.out.println("★★★★★★★ [" + REPO + "] findTxStatusByIdempotencyKey END found=" + (result != null));
        return result;
    }

    @Override
    public FwTxStatus findTxStatusByKeys(String guid, String transactionId, String idempotencyKey) {
        System.out.println("★★★★★★★ [" + REPO + "] findTxStatusByKeys START transactionId=" + transactionId);
        FwTxStatus result = mapper.selectTxStatusByKeys(guid, transactionId, idempotencyKey); // DB SELECT 복합 키
        System.out.println("★★★★★★★ [" + REPO + "] findTxStatusByKeys END found=" + (result != null));
        return result;
    }

    @Override
    public void insertTxLog(FwTxLog row) {
        System.out.println("★★★★★★★ [" + REPO + "] insertTxLog START guid=" + row.getGuid());
        mapper.insertTxLog(row); // DB INSERT: fw_tx_log
        System.out.println("★★★★★★★ [" + REPO + "] insertTxLog END logId=" + row.getLogId());
    }

    @Override
    public FwTxLog findTxLogById(Long logId) {
        System.out.println("★★★★★★★ [" + REPO + "] findTxLogById START logId=" + logId);
        FwTxLog result = mapper.selectTxLogById(logId); // DB SELECT by logId
        System.out.println("★★★★★★★ [" + REPO + "] findTxLogById END logId=" + logId
                + " found=" + (result != null));
        return result;
    }

    @Override
    public List<FwTxLog> findTxLogs(FwLogSearchDDTO criteria) {
        System.out.println("★★★★★★★ [" + REPO + "] findTxLogs START pageNo=" + criteria.getSafePageNo());
        List<FwTxLog> result = mapper.selectTxLogs(criteria); // DB SELECT 목록 (페이징)
        System.out.println("★★★★★★★ [" + REPO + "] findTxLogs END size=" + result.size());
        return result;
    }

    @Override
    public long countTxLogs(FwLogSearchDDTO criteria) {
        System.out.println("★★★★★★★ [" + REPO + "] countTxLogs START");
        long total = mapper.countTxLogs(criteria); // DB COUNT
        System.out.println("★★★★★★★ [" + REPO + "] countTxLogs END total=" + total);
        return total;
    }

    @Override
    public int deleteTxLogById(Long logId) {
        System.out.println("★★★★★★★ [" + REPO + "] deleteTxLogById START logId=" + logId);
        int deleted = mapper.deleteTxLogById(logId); // DB DELETE
        System.out.println("★★★★★★★ [" + REPO + "] deleteTxLogById END logId=" + logId + " deleted=" + deleted);
        return deleted;
    }

    @Override
    public void insertAuditLog(FwAuditLog row) {
        System.out.println("★★★★★★★ [" + REPO + "] insertAuditLog START guid=" + row.getGuid());
        mapper.insertAuditLog(row); // DB INSERT: fw_audit_log
        System.out.println("★★★★★★★ [" + REPO + "] insertAuditLog END auditId=" + row.getAuditId());
    }

    @Override
    public List<FwAuditLog> findAuditLogs(FwLogSearchDDTO criteria) {
        System.out.println("★★★★★★★ [" + REPO + "] findAuditLogs START pageNo=" + criteria.getSafePageNo());
        List<FwAuditLog> result = mapper.selectAuditLogs(criteria); // DB SELECT 목록 (페이징)
        System.out.println("★★★★★★★ [" + REPO + "] findAuditLogs END size=" + result.size());
        return result;
    }

    @Override
    public long countAuditLogs(FwLogSearchDDTO criteria) {
        System.out.println("★★★★★★★ [" + REPO + "] countAuditLogs START");
        long total = mapper.countAuditLogs(criteria); // DB COUNT
        System.out.println("★★★★★★★ [" + REPO + "] countAuditLogs END total=" + total);
        return total;
    }
}
