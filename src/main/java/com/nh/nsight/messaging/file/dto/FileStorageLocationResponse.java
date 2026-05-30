package com.nh.nsight.messaging.file.dto;

public record FileStorageLocationResponse(
        String storageBasePath,
        String storagePathPattern,
        String configuredPath,
        long maxFileSizeBytes,
        String maxFileSizeLabel,
        String allowedExtensions
) {
}
