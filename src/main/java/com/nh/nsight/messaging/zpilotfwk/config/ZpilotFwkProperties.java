package com.nh.nsight.messaging.zpilotfwk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zpilotfwk")
public class ZpilotFwkProperties {

    private final Transaction transaction = new Transaction();

    public Transaction getTransaction() {
        return transaction;
    }

    public static class Transaction {
        /** container | usertransaction */
        private String defaultMode = "container";

        public String getDefaultMode() {
            return defaultMode;
        }

        public void setDefaultMode(String defaultMode) {
            this.defaultMode = defaultMode;
        }
    }
}
