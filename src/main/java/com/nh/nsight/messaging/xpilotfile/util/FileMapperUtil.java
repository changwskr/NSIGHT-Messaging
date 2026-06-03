package com.nh.nsight.messaging.xpilotfile.util;

import com.nh.nsight.messaging.xpilotfile.dc.filedc.XpfFile;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileDDTO;

public final class FileMapperUtil {

    private FileMapperUtil() {
    }

    public static FileDDTO toDDto(XpfFile entity) {
        if (entity == null) {
            return null;
        }
        FileDDTO dto = new FileDDTO();
        dto.setFileId(entity.getFileId());
        dto.setOriginalName(entity.getOriginalName());
        dto.setStoredName(entity.getStoredName());
        dto.setContentType(entity.getContentType());
        dto.setFileSize(entity.getFileSize());
        dto.setStoragePath(entity.getStoragePath());
        dto.setBizCategory(entity.getBizCategory());
        dto.setDescription(entity.getDescription());
        dto.setUseYn(entity.getUseYn());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static XpfFile toEntity(FileDDTO dto) {
        if (dto == null) {
            return null;
        }
        XpfFile entity = new XpfFile();
        entity.setFileId(dto.getFileId());
        entity.setOriginalName(dto.getOriginalName());
        entity.setStoredName(dto.getStoredName());
        entity.setContentType(dto.getContentType());
        entity.setFileSize(dto.getFileSize());
        entity.setStoragePath(dto.getStoragePath());
        entity.setBizCategory(dto.getBizCategory());
        entity.setDescription(dto.getDescription());
        entity.setUseYn(dto.getUseYn());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
