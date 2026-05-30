package com.nh.nsight.messaging.file.service;

import com.nh.nsight.messaging.common.context.RequestContext;
import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.config.FileStorageProperties;
import com.nh.nsight.messaging.file.dao.FileDao;
import com.nh.nsight.messaging.file.dto.FileDownloadPayload;
import com.nh.nsight.messaging.file.dto.FileResponse;
import com.nh.nsight.messaging.file.dto.FileSearchCondition;
import com.nh.nsight.messaging.file.dto.FileStorageLocationResponse;
import com.nh.nsight.messaging.file.rule.FileRule;
import com.nh.nsight.messaging.file.thing.FileDocument;
import com.nh.nsight.messaging.file.util.MultipartFilenameDecoder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final FileDao fileDao;
    private final FileRule fileRule;
    private final FileStorageProperties properties;

    public FileService(FileDao fileDao, FileRule fileRule, FileStorageProperties properties) {
        this.fileDao = fileDao;
        this.fileRule = fileRule;
        this.properties = properties;
    }

    public FileResponse upload(MultipartFile file, String bizCategory, String description) {
        fileRule.validateUpload(file, bizCategory);
        String userId = RequestContext.get().userId();

        try {
            String originalName = sanitizeFilename(MultipartFilenameDecoder.decode(file.getOriginalFilename()));
            String extension = extractExtension(originalName);
            String storedName = UUID.randomUUID() + "." + extension;
            String relativePath = DATE_DIR.format(LocalDate.now()) + "/" + storedName;
            Path targetPath = storageRoot().resolve(relativePath);
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            FileDocument document = FileDocument.create(
                    originalName,
                    storedName,
                    StringUtils.hasText(file.getContentType()) ? file.getContentType() : "application/octet-stream",
                    file.getSize(),
                    relativePath,
                    bizCategory,
                    description,
                    userId
            );
            fileDao.insert(document);
            return toResponse(document);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.SYS_UNKNOWN, "파일 저장 실패: " + ex.getMessage());
        }
    }

    public FileResponse getFile(Long fileId) {
        FileDocument document = fileDao.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BIZ_NO_DATA, "fileId=" + fileId));
        return toResponse(document);
    }

    public List<FileResponse> searchFiles(FileSearchCondition condition) {
        return fileDao.findFiles(condition).stream()
                .map(this::toResponse)
                .toList();
    }

    public FileStorageLocationResponse getStorageLocation() {
        Path base = storageRoot();
        return new FileStorageLocationResponse(
                base.toString(),
                base + "/yyyy/MM/dd/{storedName}",
                properties.getStoragePath(),
                properties.getMaxFileSizeBytes(),
                formatSizeLabel(properties.getMaxFileSizeBytes()),
                properties.getAllowedExtensions()
        );
    }

    public long countFiles(FileSearchCondition condition) {
        return fileDao.countFiles(condition);
    }

    public FileDownloadPayload download(Long fileId) {
        FileDocument document = fileDao.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BIZ_NO_DATA, "fileId=" + fileId));
        if (!"Y".equals(document.getUseYn())) {
            throw new BusinessException(ErrorCode.FILE_INVALID, "비활성 파일입니다. fileId=" + fileId);
        }

        Path path = resolvePhysicalPath(document);
        if (!Files.exists(path)) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND, path.toString());
        }
        Resource resource = new FileSystemResource(path);
        return new FileDownloadPayload(resource, document.getOriginalName(), document.getContentType(), document.getFileSize());
    }

    public FileResponse updateUseYn(Long fileId, String useYn) {
        fileRule.validateUseYn(useYn);
        FileDocument document = fileDao.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BIZ_NO_DATA, "fileId=" + fileId));
        fileDao.updateUseYn(fileId, useYn, RequestContext.get().userId());
        document.setUseYn(useYn);
        return getFile(fileId);
    }

    public void deleteFile(Long fileId) {
        FileDocument document = fileDao.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BIZ_NO_DATA, "fileId=" + fileId));

        Path path = resolvePhysicalPath(document);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.SYS_UNKNOWN, "파일 삭제 실패: " + ex.getMessage());
        }
        int deleted = fileDao.deleteById(fileId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "fileId=" + fileId);
        }
    }

    private Path storageRoot() {
        return Paths.get(properties.getStoragePath()).toAbsolutePath().normalize();
    }

    private Path resolvePhysicalPath(FileDocument document) {
        return storageRoot().resolve(document.getStoragePath()).normalize();
    }

    private FileResponse toResponse(FileDocument document) {
        Path fullPath = resolvePhysicalPath(document);
        return FileResponse.from(document, storageRoot().toString(), fullPath.toString());
    }

    private String formatSizeLabel(long bytes) {
        if (bytes < 1024 * 1024) {
            return (bytes / 1024) + " KB";
        }
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private String sanitizeFilename(String filename) {
        return filename.replace("\\", "_").replace("/", "_").trim();
    }

    private String extractExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index < 0) {
            return "bin";
        }
        return filename.substring(index + 1).toLowerCase();
    }
}
