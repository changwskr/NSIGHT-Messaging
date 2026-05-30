package com.nh.nsight.messaging.file.controller;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.file.dto.FileDownloadPayload;
import com.nh.nsight.messaging.file.dto.FileResponse;
import com.nh.nsight.messaging.file.dto.FileSearchCondition;
import com.nh.nsight.messaging.file.dto.FileStorageLocationResponse;
import com.nh.nsight.messaging.file.facade.FileFacade;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileFacade fileFacade;

    public FileController(FileFacade fileFacade) {
        this.fileFacade = fileFacade;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StandardResponse<FileResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "GENERAL") String bizCategory,
            @RequestParam(required = false) String description
    ) {
        FileResponse response = fileFacade.upload(file, bizCategory, description);
        return StandardResponse.success("FILE-UPLOAD-001", "fileUpload", response);
    }

    @GetMapping("/{fileId}")
    public StandardResponse<FileResponse> getFile(@PathVariable Long fileId) {
        FileResponse response = fileFacade.getFile(fileId);
        return StandardResponse.success("FILE-DETAIL-001", "fileDetail", response);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) {
        FileDownloadPayload payload = fileFacade.download(fileId);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(payload.originalName(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType(payload.contentType()))
                .contentLength(payload.fileSize())
                .body(payload.resource());
    }

    @GetMapping("/storage-location")
    public StandardResponse<FileStorageLocationResponse> getStorageLocation() {
        FileStorageLocationResponse response = fileFacade.getStorageLocation();
        return StandardResponse.success("FILE-STORAGE-001", "fileStorageLocation", response);
    }

    @GetMapping
    public StandardResponse<List<FileResponse>> searchFiles(
            @RequestParam(required = false) String originalName,
            @RequestParam(required = false) String bizCategory,
            @RequestParam(required = false) String useYn
    ) {
        FileSearchCondition condition = new FileSearchCondition(originalName, bizCategory, useYn);
        List<FileResponse> response = fileFacade.searchFiles(condition);
        long totalCount = fileFacade.countFiles(condition);
        return StandardResponse.successPage("FILE-LIST-001", "fileList", response, 1, response.size(), totalCount);
    }

    @PutMapping("/{fileId}/use-yn")
    public StandardResponse<FileResponse> updateUseYn(
            @PathVariable Long fileId,
            @RequestParam String useYn
    ) {
        FileResponse response = fileFacade.updateUseYn(fileId, useYn);
        return StandardResponse.success("FILE-UPDATE-001", "fileUpdate", response);
    }

    @DeleteMapping("/{fileId}")
    public StandardResponse<Void> deleteFile(@PathVariable Long fileId) {
        fileFacade.deleteFile(fileId);
        return StandardResponse.success("FILE-DELETE-001", "fileDelete", null);
    }
}
