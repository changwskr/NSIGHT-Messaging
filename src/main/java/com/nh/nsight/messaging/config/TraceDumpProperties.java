package com.nh.nsight.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nsight.trace-dump")
public class TraceDumpProperties {

    private String evidencePath = "./data/trace-dump-evidence";

    public String getEvidencePath() {
        return evidencePath;
    }

    public void setEvidencePath(String evidencePath) {
        this.evidencePath = evidencePath;
    }
}
