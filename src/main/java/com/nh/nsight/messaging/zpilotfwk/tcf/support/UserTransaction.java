package com.nh.nsight.messaging.zpilotfwk.tcf.support;

public interface UserTransaction {

    void begin() throws Exception;

    void commit() throws Exception;

    void rollback() throws Exception;

    void setTransactionTimeout(int seconds) throws Exception;
}
