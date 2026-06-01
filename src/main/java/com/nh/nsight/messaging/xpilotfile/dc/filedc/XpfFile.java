package com.nh.nsight.messaging.xpilotfile.dc.filedc;

import java.time.LocalDateTime;

public class XpfFile {

    private Long fileId;
    private String originalName;
    private String storedName;
    private String contentType;
    private long fileSize;
    private String storagePath;
    private String bizCategory;
    private String description;
    private String useYn;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public static XpfFile create(String originalName, String storedName, String contentType, long fileSize,
                                 String storagePath, String bizCategory, String description, String userId) {
        XpfFile file = new XpfFile();
        file.originalName = originalName;
        file.storedName = storedName;
        file.contentType = contentType;
        file.fileSize = fileSize;
        file.storagePath = storagePath;
        file.bizCategory = bizCategory;
        file.description = description;
        file.useYn = "Y";
        file.createdBy = userId;
        file.updatedBy = userId;
        return file;
    }

    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public String getBizCategory() { return bizCategory; }
    public void setBizCategory(String bizCategory) { this.bizCategory = bizCategory; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUseYn() { return useYn; }
    public void setUseYn(String useYn) { this.useYn = useYn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
