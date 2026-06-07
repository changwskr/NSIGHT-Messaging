package com.nh.nsight.messaging.zpilotfwk.tcf.support;

public class ConfigElement {

    private final String text;

    public ConfigElement(String text) {
        this.text = text;
    }

    public String getTextTrim() {
        return text == null ? "" : text.trim();
    }
}
