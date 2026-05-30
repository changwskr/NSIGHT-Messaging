package com.nh.nsight.messaging.file.dto;

public class FileSearchCondition {
    private String originalName;
    private String bizCategory;
    private String useYn;

    public FileSearchCondition() {
    }

    public FileSearchCondition(String originalName, String bizCategory, String useYn) {
        this.originalName = originalName;
        this.bizCategory = bizCategory;
        this.useYn = useYn;
    }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getBizCategory() { return bizCategory; }
    public void setBizCategory(String bizCategory) { this.bizCategory = bizCategory; }
    public String getUseYn() { return useYn; }
    public void setUseYn(String useYn) { this.useYn = useYn; }
}
