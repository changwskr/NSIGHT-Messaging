package com.nh.nsight.messaging.xpilotfile.ac.fileac.dto;

import com.nh.nsight.messaging.file.dto.FileResponse;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileDDTO;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileSearchDDTO;
import com.nh.nsight.messaging.xpilotfile.zcommonutil.XpilotFileStorageSupport;

import java.util.ArrayList;
import java.util.List;

public final class FileCDtoConverter {

    private static final String API_FILES = "/api/xpilotfile/files";

    private FileCDtoConverter() {
    }

    public static FileDDTO toDDto(FileCDTO source) {
        if (source == null) {
            return null;
        }
        FileDDTO target = new FileDDTO();
        target.setFileId(source.getFileId());
        target.setOriginalName(source.getOriginalName());
        target.setStoredName(source.getStoredName());
        target.setContentType(source.getContentType());
        target.setFileSize(source.getFileSize());
        target.setStoragePath(source.getStoragePath());
        target.setBizCategory(source.getBizCategory());
        target.setDescription(source.getDescription());
        target.setUseYn(source.getUseYn());
        target.setCreatedBy(source.getCreatedBy());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setUpdatedAt(source.getUpdatedAt());
        return target;
    }

    public static FileCDTO toCDto(FileDDTO source) {
        if (source == null) {
            return null;
        }
        FileCDTO target = new FileCDTO();
        target.setFileId(source.getFileId());
        target.setOriginalName(source.getOriginalName());
        target.setStoredName(source.getStoredName());
        target.setContentType(source.getContentType());
        target.setFileSize(source.getFileSize());
        target.setStoragePath(source.getStoragePath());
        target.setBizCategory(source.getBizCategory());
        target.setDescription(source.getDescription());
        target.setUseYn(source.getUseYn());
        target.setCreatedBy(source.getCreatedBy());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setUpdatedAt(source.getUpdatedAt());
        return target;
    }

    public static FileCDTO enrichPaths(FileCDTO dto, XpilotFileStorageSupport storageSupport) {
        if (dto == null) {
            return null;
        }
        FileDDTO ddto = toDDto(dto);
        dto.setStorageBasePath(storageSupport.storageRoot().toString());
        dto.setStorageFullPath(storageSupport.resolvePhysicalPath(ddto).toString());
        return dto;
    }

    public static FileSearchDDTO toSearchDDto(FileSearchCDTO source) {
        if (source == null) {
            return null;
        }
        FileSearchDDTO target = new FileSearchDDTO();
        target.setOriginalName(source.getOriginalName());
        target.setBizCategory(source.getBizCategory());
        target.setUseYn(source.getUseYn());
        return target;
    }

    public static List<FileCDTO> toCDtoList(List<FileDDTO> sources) {
        List<FileCDTO> list = new ArrayList<>();
        if (sources == null) {
            return list;
        }
        for (FileDDTO source : sources) {
            list.add(toCDto(source));
        }
        return list;
    }

    public static FileResponse toResponse(FileCDTO dto, XpilotFileStorageSupport storageSupport) {
        if (dto == null) {
            return null;
        }
        FileCDTO enriched = enrichPaths(dto, storageSupport);
        return new FileResponse(
                enriched.getFileId(),
                enriched.getOriginalName(),
                enriched.getStoredName(),
                enriched.getContentType(),
                enriched.getFileSize(),
                storageSupport.formatSizeLabel(enriched.getFileSize()),
                enriched.getStoragePath(),
                enriched.getStorageBasePath(),
                enriched.getStoragePath(),
                enriched.getStorageFullPath(),
                enriched.getBizCategory(),
                enriched.getDescription(),
                enriched.getUseYn(),
                API_FILES + "/" + enriched.getFileId() + "/download",
                enriched.getCreatedBy(),
                enriched.getCreatedAt(),
                enriched.getUpdatedBy(),
                enriched.getUpdatedAt()
        );
    }

    public static List<FileResponse> toResponseList(List<FileCDTO> dtos, XpilotFileStorageSupport storageSupport) {
        List<FileResponse> list = new ArrayList<>();
        if (dtos == null) {
            return list;
        }
        for (FileCDTO dto : dtos) {
            list.add(toResponse(dto, storageSupport));
        }
        return list;
    }
}
