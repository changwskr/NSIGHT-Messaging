package com.nh.nsight.messaging.zpilotfwk.tcf.support;

public class SessionContext {

    private boolean rollbackOnly;

    public void setRollbackOnly(boolean rollbackOnly) {
        this.rollbackOnly = rollbackOnly;
    }

    public boolean getRollbackOnly() {
        return rollbackOnly;
    }

    @Override
    public String toString() {
        return "SessionContext{rollbackOnly=" + rollbackOnly + "}";
    }
}
