package com.nh.nsight.messaging.xpilotfile.util;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.config.FileStorageProperties;
import com.nh.nsight.messaging.file.util.MultipartFilenameDecoder;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class XpilotFileRule {

    private static final Set<String> BIZ_CATEGORIES = Set.of("GENERAL", "NOTICE", "MANUAL", "ATTACHMENT");

    private final FileStorageProperties properties;

    public XpilotFileRule(FileStorageProperties properties) {
        this.properties = properties;
    }

    public void validateUpload(MultipartFile file, String bizCategory) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_INVALID, "업로드 파일이 비어 있습니다.");
        }
        if (file.getSize() > properties.getMaxFileSizeBytes()) {
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE,
                    "max=" + properties.getMaxFileSizeBytes() + ", actual=" + file.getSize());
        }
        String originalName = MultipartFilenameDecoder.decode(file.getOriginalFilename());
        if (!StringUtils.hasText(originalName)) {
            throw new BusinessException(ErrorCode.FILE_INVALID, "파일명이 없습니다.");
        }
        String extension = extractExtension(originalName);
        if (!properties.allowedExtensionSet().contains(extension)) {
            throw new BusinessException(ErrorCode.FILE_INVALID, "허용되지 않은 확장자: " + extension);
        }
        if (!StringUtils.hasText(bizCategory) || !BIZ_CATEGORIES.contains(bizCategory)) {
            throw new BusinessException(ErrorCode.VAL_INVALID_REQUEST,
                    "bizCategory는 GENERAL, NOTICE, MANUAL, ATTACHMENT 중 하나여야 합니다.");
        }
    }

    public void validateUseYn(String useYn) {
        if (!Set.of("Y", "N").contains(useYn)) {
            throw new BusinessException(ErrorCode.VAL_INVALID_REQUEST, "useYn은 Y 또는 N이어야 합니다.");
        }
    }

    private String extractExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index < 0 || index == filename.length() - 1) {
            return "";
        }
        return filename.substring(index + 1).toLowerCase();
    }
}
