package com.nh.nsight.messaging.file.facade;

import com.nh.nsight.messaging.file.dto.FileDownloadPayload;
import com.nh.nsight.messaging.file.dto.FileResponse;
import com.nh.nsight.messaging.file.dto.FileSearchCondition;
import com.nh.nsight.messaging.file.dto.FileStorageLocationResponse;
import com.nh.nsight.messaging.file.service.FileService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileFacade {

    private final FileService fileService;

    public FileFacade(FileService fileService) {
        this.fileService = fileService;
    }

    @Transactional(timeout = 10)
    public FileResponse upload(MultipartFile file, String bizCategory, String description) {
        return fileService.upload(file, bizCategory, description);
    }

    @Transactional(readOnly = true, timeout = 3)
    public FileResponse getFile(Long fileId) {
        return fileService.getFile(fileId);
    }

    @Transactional(readOnly = true, timeout = 3)
    public List<FileResponse> searchFiles(FileSearchCondition condition) {
        return fileService.searchFiles(condition);
    }

    @Transactional(readOnly = true, timeout = 3)
    public long countFiles(FileSearchCondition condition) {
        return fileService.countFiles(condition);
    }

    @Transactional(readOnly = true, timeout = 3)
    public FileStorageLocationResponse getStorageLocation() {
        return fileService.getStorageLocation();
    }

    @Transactional(readOnly = true, timeout = 5)
    public FileDownloadPayload download(Long fileId) {
        return fileService.download(fileId);
    }

    @Transactional(timeout = 5)
    public FileResponse updateUseYn(Long fileId, String useYn) {
        return fileService.updateUseYn(fileId, useYn);
    }

    @Transactional(timeout = 5)
    public void deleteFile(Long fileId) {
        fileService.deleteFile(fileId);
    }
}
