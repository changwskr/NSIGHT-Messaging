package com.nh.nsight.messaging.zpilotfwk.tcf.support;

public final class TxHelper {

    private static final TransactionManager TRANSACTION_MANAGER = new TransactionManager();

    private TxHelper() {
    }

    public static TransactionManager getTransactionManager() {
        return TRANSACTION_MANAGER;
    }

    public static String status2String(int status) {
        return switch (status) {
            case Transaction.STATUS_ACTIVE -> "ACTIVE(0)";
            case Transaction.STATUS_MARKED_ROLLBACK -> "MARKED_ROLLBACK(1)";
            case Transaction.STATUS_NONE -> "NONE(6)";
            case Transaction.STATUS_ERROR -> "ERROR(-1)";
            default -> "UNKNOWN(" + status + ")";
        };
    }
}
