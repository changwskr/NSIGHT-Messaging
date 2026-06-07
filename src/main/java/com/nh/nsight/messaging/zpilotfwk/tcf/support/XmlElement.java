package com.nh.nsight.messaging.zpilotfwk.tcf.support;

import java.util.HashMap;
import java.util.Map;

public class XmlElement {

    private final Map<String, String> children = new HashMap<>();

    public XmlElement child(String name, String value) {
        children.put(name, value);
        return this;
    }

    public XmlElement getChild(String name) {
        return this;
    }

    public String getChildTextTrim(String name) {
        return children.getOrDefault(name, "off");
    }
}
