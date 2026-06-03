package com.nh.nsight.messaging.xpilotfile.as.fileas;

import com.nh.nsight.messaging.common.context.RequestContext;
import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.file.util.MultipartFilenameDecoder;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileCDTO;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileCDtoConverter;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.DCFile;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileDDTO;
import com.nh.nsight.messaging.xpilotfile.util.XpilotFileRule;
import com.nh.nsight.messaging.xpilotfile.util.XpilotFileStorageSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ASMXPF71001 {

    private static final String AS = "ASMXPF71001";
    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final DCFile dcFile;
    private final XpilotFileRule fileRule;
    private final XpilotFileStorageSupport storageSupport;

    public ASMXPF71001(DCFile dcFile, XpilotFileRule fileRule, XpilotFileStorageSupport storageSupport) {
        this.dcFile = dcFile;
        this.fileRule = fileRule;
        this.storageSupport = storageSupport;
    }

    @Transactional(timeout = 5)
    public FileCDTO upload(MultipartFile file, String bizCategory, String description) {
        System.out.println("★★★★★★★ [" + AS + "] upload START bizCategory=" + bizCategory);
        fileRule.validateUpload(file, bizCategory);
        String userId = RequestContext.get().userId();

        try {
            String originalName = storageSupport.sanitizeFilename(
                    MultipartFilenameDecoder.decode(file.getOriginalFilename()));
            String extension = storageSupport.extractExtension(originalName);
            String storedName = UUID.randomUUID() + "." + extension;
            String relativePath = DATE_DIR.format(LocalDate.now()) + "/" + storedName;
            Path targetPath = storageSupport.storageRoot().resolve(relativePath);
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            FileDDTO ddto = new FileDDTO();
            ddto.setOriginalName(originalName);
            ddto.setStoredName(storedName);
            ddto.setContentType(StringUtils.hasText(file.getContentType())
                    ? file.getContentType()
                    : "application/octet-stream");
            ddto.setFileSize(file.getSize());
            ddto.setStoragePath(relativePath);
            ddto.setBizCategory(bizCategory);
            ddto.setDescription(description);
            ddto.setUseYn("Y");
            ddto.setCreatedBy(userId);
            ddto.setUpdatedBy(userId);
            dcFile.createFile(ddto);

            FileCDTO result = FileCDtoConverter.toCDto(dcFile.getFile(ddto.getFileId()));
            FileCDTO enriched = FileCDtoConverter.enrichPaths(result, storageSupport);
            System.out.println("★★★★★★★ [" + AS + "] upload END fileId="
                    + (enriched != null ? enriched.getFileId() : null));
            return enriched;
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.SYS_UNKNOWN, "파일 저장 실패: " + ex.getMessage());
        }
    }
}
