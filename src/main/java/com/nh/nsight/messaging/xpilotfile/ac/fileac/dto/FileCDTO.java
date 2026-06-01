package com.nh.nsight.messaging.xpilotfile.ac.fileac.dto;

import java.time.LocalDateTime;

public class FileCDTO {

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
    private String storageBasePath;
    private String storageFullPath;

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
    public String getStorageBasePath() { return storageBasePath; }
    public void setStorageBasePath(String storageBasePath) { this.storageBasePath = storageBasePath; }
    public String getStorageFullPath() { return storageFullPath; }
    public void setStorageFullPath(String storageFullPath) { this.storageFullPath = storageFullPath; }
}
