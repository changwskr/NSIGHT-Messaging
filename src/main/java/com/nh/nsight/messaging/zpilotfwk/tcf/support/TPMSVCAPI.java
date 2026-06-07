package com.nh.nsight.messaging.zpilotfwk.tcf.support;

import com.nh.nsight.messaging.zpilotfwk.config.ZpilotFwkProperties;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 레거시 STF/ETF 트랜잭션 정보 API.
 * commit/rollback은 TCF {@code @Transactional} 경계(Spring)에서만 수행한다.
 * 이 클래스는 TPinfo·참여 상태 추적(점검) 용도로만 사용한다.
 */
@Service
public class TPMSVCAPI {

    private static final Object INSPECTION_MARKER = new Object();

    private static TPMSVCAPI instance;

    private final String defaultTransactionMode;
    private final ThreadLocal<Object> transactionHandle = new ThreadLocal<>();

    @Autowired
    public TPMSVCAPI(ZpilotFwkProperties properties) {
        this.defaultTransactionMode = properties.getTransaction().getDefaultMode();
    }

    /** {@code main()} 로컬 실행용 — Spring Bean 없이 초기화 */
    private TPMSVCAPI(String defaultTransactionMode) {
        this.defaultTransactionMode = defaultTransactionMode;
    }

    @PostConstruct
    void bindSpringInstance() {
        instance = this;
    }

    public static TPMSVCAPI createLocal(String defaultTransactionMode) {
        TPMSVCAPI api = new TPMSVCAPI(defaultTransactionMode);
        instance = api;
        return api;
    }

    public static void bindInstance(TPMSVCAPI api) {
        instance = api;
    }

    public static TPMSVCAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TPMSVCAPI is not initialized.");
        }
        return instance;
    }

    public UserTransaction TPJNDIUserTransaction(String url) {
        return new SimpleUserTransaction();
    }

    public String TPgetbeantransactiontype(EPlatonEvent event) {
        return defaultTransactionMode;
    }

    public int TPinfo() {
        return TPinfo(null);
    }

    public int TPinfo(UserTransaction tx) {
        try {
            if (isSpringTransactionActive()) {
                if (TransactionAspectSupport.currentTransactionStatus().isRollbackOnly()) {
                    return Transaction.STATUS_MARKED_ROLLBACK;
                }
                return Transaction.STATUS_ACTIVE;
            }
        } catch (Exception ex) {
            return Transaction.STATUS_ERROR;
        }
        return TxHelper.getTransactionManager().getStatus();
    }

    /** 트랜잭션 점검 등록. 실제 begin/commit/rollback은 Spring이 수행한다. */
    public boolean TPbegin(String timer) {
        if (transactionHandle.get() != null) {
            return true;
        }
        bindInspectionMarker();
        return true;
    }

    public boolean TPbegin(UserTransaction tx, int timer) {
        return TPbegin(String.valueOf(timer));
    }

    /** 점검 등록 상태만 해제 (commit 아님) */
    public void clearInspectionState() {
        clearInspectionMarker();
    }

    /** @deprecated commit은 Spring TCF에서 수행. {@link #clearInspectionState()} 사용 */
    public boolean TPcommit() {
        clearInspectionMarker();
        return true;
    }

    public boolean TPcommit(UserTransaction tx) {
        return TPcommit();
    }

    /** rollback은 Spring TCF {@code @Transactional} 경계에서 수행 — 여기서는 추적 상태만 정리 */
    public boolean TProllback(SessionContext ctx) {
        return TProllback();
    }

    public boolean TProllback(SessionContext ctx, EPlatonEvent event) {
        return TProllback();
    }

    public boolean TProllback(UserTransaction tx) {
        return TProllback();
    }

    private boolean TProllback() {
        clearInspectionMarker();
        return true;
    }

    public String describeTransactionStatus() {
        return TxHelper.status2String(TPinfo());
    }

    /** STF/ETF에서 Spring {@code @Transactional} 경계 여부 판단용 */
    public boolean isSpringManagedTransaction() {
        return isSpringTransactionActive();
    }

    private void bindInspectionMarker() {
        transactionHandle.set(INSPECTION_MARKER);
        TxHelper.getTransactionManager().bindActive(defaultTransactionMode, INSPECTION_MARKER);
    }

    private void clearInspectionMarker() {
        transactionHandle.remove();
        TxHelper.getTransactionManager().clear();
    }

    private boolean isSpringTransactionActive() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }
}
