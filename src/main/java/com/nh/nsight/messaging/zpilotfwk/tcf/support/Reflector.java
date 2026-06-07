package com.nh.nsight.messaging.zpilotfwk.tcf.support;

public final class Reflector {

    private Reflector() {
    }

    public static String objectToString(Object target) {
        return target == null ? "null" : target.toString();
    }
}
