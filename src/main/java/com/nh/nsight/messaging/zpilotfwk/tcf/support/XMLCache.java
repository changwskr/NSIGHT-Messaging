package com.nh.nsight.messaging.zpilotfwk.tcf.support;

public final class XMLCache {

    private static final XMLCache INSTANCE = new XMLCache();

    private XMLCache() {
    }

    public static XMLCache getInstance() {
        return INSTANCE;
    }

    public XmlDocument getXML(String fileName) {
        return new XmlDocument();
    }
}
