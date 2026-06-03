package com.nh.nsight.messaging.xpilotfile.as.fileas;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.config.FileStorageProperties;
import com.nh.nsight.messaging.file.dto.FileDownloadPayload;
import com.nh.nsight.messaging.file.dto.FileStorageLocationResponse;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileCDTO;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileCDtoConverter;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileSearchCDTO;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.DCFile;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileDDTO;
import com.nh.nsight.messaging.xpilotfile.util.XpilotFileStorageSupport;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ASMXPF72001 {

    private static final String AS = "ASMXPF72001";

    private final DCFile dcFile;
    private final XpilotFileStorageSupport storageSupport;
    private final FileStorageProperties properties;

    public ASMXPF72001(DCFile dcFile, XpilotFileStorageSupport storageSupport, FileStorageProperties properties) {
        this.dcFile = dcFile;
        this.storageSupport = storageSupport;
        this.properties = properties;
    }

    @Transactional(readOnly = true, timeout = 3)
    public FileCDTO get(Long fileId) {
        System.out.println("★★★★★★★ [" + AS + "] get START fileId=" + fileId);
        FileDDTO ddto = dcFile.getFile(fileId);
        if (ddto == null) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "fileId=" + fileId);
        }
        FileCDTO cdto = FileCDtoConverter.toCDto(ddto);
        FileCDTO result = FileCDtoConverter.enrichPaths(cdto, storageSupport);
        System.out.println("★★★★★★★ [" + AS + "] get END fileId=" + fileId);
        return result;
    }

    @Transactional(readOnly = true, timeout = 3)
    public List<FileCDTO> search(FileSearchCDTO criteria) {
        System.out.println("★★★★★★★ [" + AS + "] search START");
        List<FileCDTO> list = FileCDtoConverter.toCDtoList(
                dcFile.searchFiles(FileCDtoConverter.toSearchDDto(criteria)));
        for (FileCDTO cdto : list) {
            FileCDtoConverter.enrichPaths(cdto, storageSupport);
        }
        System.out.println("★★★★★★★ [" + AS + "] search END size=" + list.size());
        return list;
    }

    @Transactional(readOnly = true, timeout = 3)
    public long count(FileSearchCDTO criteria) {
        System.out.println("★★★★★★★ [" + AS + "] count START");
        long total = dcFile.countFiles(FileCDtoConverter.toSearchDDto(criteria));
        System.out.println("★★★★★★★ [" + AS + "] count END total=" + total);
        return total;
    }

    @Transactional(readOnly = true, timeout = 3)
    public FileStorageLocationResponse getStorageLocation() {
        System.out.println("★★★★★★★ [" + AS + "] getStorageLocation START");
        Path base = storageSupport.storageRoot();
        FileStorageLocationResponse result = new FileStorageLocationResponse(
                base.toString(),
                base + "/yyyy/MM/dd/{storedName}",
                properties.getStoragePath(),
                properties.getMaxFileSizeBytes(),
                storageSupport.formatSizeLabel(properties.getMaxFileSizeBytes()),
                properties.getAllowedExtensions()
        );
        System.out.println("★★★★★★★ [" + AS + "] getStorageLocation END");
        return result;
    }

    @Transactional(readOnly = true, timeout = 3)
    public FileDownloadPayload download(Long fileId) {
        System.out.println("★★★★★★★ [" + AS + "] download START fileId=" + fileId);
        FileDDTO ddto = dcFile.getFile(fileId);
        if (ddto == null) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "fileId=" + fileId);
        }
        if (!"Y".equals(ddto.getUseYn())) {
            throw new BusinessException(ErrorCode.FILE_INVALID, "비활성 파일입니다. fileId=" + fileId);
        }
        Path path = storageSupport.resolvePhysicalPath(ddto);
        if (!Files.exists(path)) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND, path.toString());
        }
        Resource resource = new FileSystemResource(path);
        FileDownloadPayload result = new FileDownloadPayload(
                resource, ddto.getOriginalName(), ddto.getContentType(), ddto.getFileSize());
        System.out.println("★★★★★★★ [" + AS + "] download END fileId=" + fileId);
        return result;
    }
}
