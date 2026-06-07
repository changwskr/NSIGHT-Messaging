package com.nh.nsight.messaging.zpilotfwk.tcf.support;

/**
 * 현재 스레드 트랜잭션 스냅샷 (레거시 TPinfo 코드 호환).
 */
public class Transaction {

    public static final int STATUS_ERROR = -1;
    public static final int STATUS_ACTIVE = 0;
    public static final int STATUS_MARKED_ROLLBACK = 1;
    public static final int STATUS_NONE = 6;

    private int status = STATUS_NONE;
    private String mode;
    private boolean active;
    private String beginTime;
    private Object handle;

    public static Transaction none() {
        Transaction tx = new Transaction();
        tx.status = STATUS_NONE;
        tx.active = false;
        return tx;
    }

    public static Transaction active(String mode, Object handle, String beginTime) {
        Transaction tx = new Transaction();
        tx.status = STATUS_ACTIVE;
        tx.mode = mode;
        tx.active = true;
        tx.handle = handle;
        tx.beginTime = beginTime;
        return tx;
    }

    public int getStatus() {
        return status;
    }

    void setStatus(int status) {
        this.status = status;
    }

    public String getMode() {
        return mode;
    }

    public boolean isActive() {
        return active;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public Object getHandle() {
        return handle;
    }

    public boolean isMarkedRollback() {
        return status == STATUS_MARKED_ROLLBACK;
    }

    @Override
    public String toString() {
        return "Transaction{status=" + status + ", mode=" + mode + ", active=" + active
                + ", beginTime=" + beginTime + "}";
    }
}
