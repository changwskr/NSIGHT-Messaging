package com.nh.nsight.messaging.zpilotfwk.tcf.support;

/**
 * 스레드별 현재 트랜잭션 정보를 관리한다.
 * {@link TPMSVCAPI}의 begin/commit/rollback과 동기화된다.
 */
public class TransactionManager {

    private final ThreadLocal<Transaction> current = new ThreadLocal<>();

    /**
     * 현재 스레드의 트랜잭션. 없으면 {@link Transaction#none()} 반환.
     */
    public Transaction getTransaction() {
        Transaction tx = current.get();
        return tx != null ? tx : Transaction.none();
    }

    /**
     * 레거시 TPinfo 코드: 6=없음, 0=활성, 1=rollback 표시, -1=오류.
     */
    public int getStatus() {
        return getTransaction().getStatus();
    }

    public boolean isActive() {
        return getTransaction().isActive();
    }

    public String getMode() {
        return getTransaction().getMode();
    }

    public void activate(String mode, Object handle) {
        bindActive(mode, handle);
    }

    void bindActive(String mode, Object handle) {
        current.set(Transaction.active(mode, handle, CommonUtil.GetSysTime()));
    }

    public void markRollbackOnly() {
        markRollback();
    }

    void markRollback() {
        Transaction tx = current.get();
        if (tx != null) {
            tx.setStatus(Transaction.STATUS_MARKED_ROLLBACK);
        }
    }

    public void deactivate() {
        clear();
    }

    void clear() {
        current.remove();
    }
}
