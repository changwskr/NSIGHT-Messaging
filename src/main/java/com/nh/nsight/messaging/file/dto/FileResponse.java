package com.nh.nsight.messaging.file.dto;

import com.nh.nsight.messaging.file.thing.FileDocument;

import java.time.LocalDateTime;

public record FileResponse(
        Long fileId,
        String originalName,
        String storedName,
        String contentType,
        long fileSize,
        String fileSizeLabel,
        String storagePath,
        String storageBasePath,
        String storageRelativePath,
        String storageFullPath,
        String bizCategory,
        String description,
        String useYn,
        String downloadUrl,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
) {
    public static FileResponse from(FileDocument document, String storageBasePath, String storageFullPath) {
        return new FileResponse(
                document.getFileId(),
                document.getOriginalName(),
                document.getStoredName(),
                document.getContentType(),
                document.getFileSize(),
                formatSize(document.getFileSize()),
                document.getStoragePath(),
                storageBasePath,
                document.getStoragePath(),
                storageFullPath,
                document.getBizCategory(),
                document.getDescription(),
                document.getUseYn(),
                "/api/v1/files/" + document.getFileId() + "/download",
                document.getCreatedBy(),
                document.getCreatedAt(),
                document.getUpdatedBy(),
                document.getUpdatedAt()
        );
    }

    private static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}
