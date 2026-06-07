package com.nh.nsight.messaging.zpilotfwk.tcf.support;

import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;

/**
 * 트랜잭션 API 스텁. Spring import 없이 main()에서도 로드·실행 가능하다.
 */
public class TPMSVCAPI {

    public interface TransactionBridge {
        Object beginTransaction();

        void commit(Object handle);

        void rollback(Object handle);
    }

    private static TPMSVCAPI instance = new TPMSVCAPI("container");

    private String defaultTransactionMode;
    private TransactionBridge transactionBridge;
    private final ThreadLocal<Object> transactionHandle = new ThreadLocal<>();

    private TPMSVCAPI(String defaultTransactionMode) {
        this.defaultTransactionMode = defaultTransactionMode;
    }

    public static TPMSVCAPI createLocal(String defaultTransactionMode) {
        TPMSVCAPI api = new TPMSVCAPI(defaultTransactionMode);
        instance = api;
        return api;
    }

    public static void bindInstance(TPMSVCAPI api) {
        instance = api;
    }

    public void attachTransactionBridge(TransactionBridge bridge) {
        this.transactionBridge = bridge;
    }

    void setDefaultTransactionMode(String defaultTransactionMode) {
        this.defaultTransactionMode = defaultTransactionMode;
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
        return TxHelper.getTransactionManager().getStatus();
    }

    public int TPinfo(UserTransaction tx) {
        return TPinfo();
    }

    public boolean TPbegin(String timer) {
        if (transactionHandle.get() != null) {
            return true;
        }
        Object handle;
        if (transactionBridge != null) {
            handle = transactionBridge.beginTransaction();
        } else {
            handle = Boolean.TRUE;
        }
        transactionHandle.set(handle);
        TxHelper.getTransactionManager().bindActive(defaultTransactionMode, handle);
        return true;
    }

    public boolean TPbegin(UserTransaction tx, int timer) {
        return TPbegin(String.valueOf(timer));
    }

    public boolean TPcommit() {
        Object handle = transactionHandle.get();
        if (handle == null) {
            return true;
        }
        if (transactionBridge != null) {
            transactionBridge.commit(handle);
        }
        transactionHandle.remove();
        TxHelper.getTransactionManager().clear();
        return true;
    }

    public boolean TPcommit(UserTransaction tx) {
        return TPcommit();
    }

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
        Object handle = transactionHandle.get();
        if (handle == null) {
            return true;
        }
        if (transactionBridge != null) {
            transactionBridge.rollback(handle);
        }
        transactionHandle.remove();
        TxHelper.getTransactionManager().clear();
        return true;
    }
}
