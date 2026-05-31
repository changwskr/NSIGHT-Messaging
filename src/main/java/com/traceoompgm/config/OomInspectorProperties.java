package com.traceoompgm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nsight.oom-inspector")
public class OomInspectorProperties {

    private String defaultSourceRoot = "./src/main/java";
    private String defaultMapperRoot = "./src/main/resources/mapper";
    private String defaultConfigPath = "./src/main/resources/application.yml";
    private String profileName = "nsight-8core-32gb";
    private boolean failOnCritical = true;

    public String getDefaultSourceRoot() {
        return defaultSourceRoot;
    }

    public void setDefaultSourceRoot(String defaultSourceRoot) {
        this.defaultSourceRoot = defaultSourceRoot;
    }

    public String getDefaultMapperRoot() {
        return defaultMapperRoot;
    }

    public void setDefaultMapperRoot(String defaultMapperRoot) {
        this.defaultMapperRoot = defaultMapperRoot;
    }

    public String getDefaultConfigPath() {
        return defaultConfigPath;
    }

    public void setDefaultConfigPath(String defaultConfigPath) {
        this.defaultConfigPath = defaultConfigPath;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public boolean isFailOnCritical() {
        return failOnCritical;
    }

    public void setFailOnCritical(boolean failOnCritical) {
        this.failOnCritical = failOnCritical;
    }
}
