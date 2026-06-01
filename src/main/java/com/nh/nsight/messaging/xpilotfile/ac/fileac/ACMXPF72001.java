package com.nh.nsight.messaging.xpilotfile.ac.fileac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.file.dto.FileDownloadPayload;
import com.nh.nsight.messaging.file.dto.FileResponse;
import com.nh.nsight.messaging.file.dto.FileStorageLocationResponse;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileCDtoConverter;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileSearchCDTO;
import com.nh.nsight.messaging.xpilotfile.as.fileas.ASMXPF72001;
import com.nh.nsight.messaging.xpilotfile.zcommonutil.XpilotFileStorageSupport;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/xpilotfile/files")
public class ACMXPF72001 {

    private static final String AC = "ACMXPF72001";

    private final ASMXPF72001 asmxpf72001;
    private final XpilotFileStorageSupport storageSupport;

    public ACMXPF72001(ASMXPF72001 asmxpf72001, XpilotFileStorageSupport storageSupport) {
        this.asmxpf72001 = asmxpf72001;
        this.storageSupport = storageSupport;
    }

    @GetMapping("/{fileId}")
    public StandardResponse<FileResponse> getFile(@PathVariable Long fileId) {
        System.out.println("★★★★★★★ [" + AC + "] getFile START fileId=" + fileId);
        FileResponse response = FileCDtoConverter.toResponse(asmxpf72001.get(fileId), storageSupport);
        StandardResponse<FileResponse> result =
                StandardResponse.success("XPF-DETAIL-001", "xpilotFileDetail", response);
        System.out.println("★★★★★★★ [" + AC + "] getFile END fileId=" + fileId);
        return result;
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) {
        System.out.println("★★★★★★★ [" + AC + "] download START fileId=" + fileId);
        FileDownloadPayload payload = asmxpf72001.download(fileId);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(payload.originalName(), StandardCharsets.UTF_8)
                .build();
        ResponseEntity<Resource> result = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType(payload.contentType()))
                .contentLength(payload.fileSize())
                .body(payload.resource());
        System.out.println("★★★★★★★ [" + AC + "] download END fileId=" + fileId);
        return result;
    }

    @GetMapping("/storage-location")
    public StandardResponse<FileStorageLocationResponse> getStorageLocation() {
        System.out.println("★★★★★★★ [" + AC + "] getStorageLocation START");
        FileStorageLocationResponse response = asmxpf72001.getStorageLocation();
        StandardResponse<FileStorageLocationResponse> result =
                StandardResponse.success("XPF-STORAGE-001", "xpilotFileStorageLocation", response);
        System.out.println("★★★★★★★ [" + AC + "] getStorageLocation END");
        return result;
    }

    @GetMapping
    public StandardResponse<List<FileResponse>> searchFiles(
            @RequestParam(required = false) String originalName,
            @RequestParam(required = false) String bizCategory,
            @RequestParam(required = false) String useYn
    ) {
        System.out.println("★★★★★★★ [" + AC + "] searchFiles START originalName=" + originalName
                + " bizCategory=" + bizCategory + " useYn=" + useYn);
        FileSearchCDTO criteria = new FileSearchCDTO();
        criteria.setOriginalName(originalName);
        criteria.setBizCategory(bizCategory);
        criteria.setUseYn(useYn);
        List<FileResponse> response = FileCDtoConverter.toResponseList(asmxpf72001.search(criteria), storageSupport);
        long totalCount = asmxpf72001.count(criteria);
        StandardResponse<List<FileResponse>> result = StandardResponse.successPage(
                "XPF-LIST-001", "xpilotFileList", response, 1, response.size(), totalCount);
        System.out.println("★★★★★★★ [" + AC + "] searchFiles END totalCount=" + totalCount);
        return result;
    }
}
