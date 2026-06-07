package com.nh.nsight.messaging.zpilotfwk.tcf.support;

public final class Config {

    private static final Config INSTANCE = new Config();

    private Config() {
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public ConfigElement getElement(String tag) {
        return new ConfigElement("config.xml");
    }
}
