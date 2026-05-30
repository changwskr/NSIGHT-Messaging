package com.nh.nsight.messaging.file.dto;

import org.springframework.core.io.Resource;

public record FileDownloadPayload(
        Resource resource,
        String originalName,
        String contentType,
        long fileSize
) {
}
