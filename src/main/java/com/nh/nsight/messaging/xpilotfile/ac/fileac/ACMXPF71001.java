package com.nh.nsight.messaging.xpilotfile.ac.fileac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.file.dto.FileResponse;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileCDtoConverter;
import com.nh.nsight.messaging.xpilotfile.as.fileas.ASMXPF71001;
import com.nh.nsight.messaging.xpilotfile.util.XpilotFileStorageSupport;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/xpilotfile/files")
public class ACMXPF71001 {

    private static final String AC = "ACMXPF71001";

    private final ASMXPF71001 asmxpf71001;
    private final XpilotFileStorageSupport storageSupport;

    public ACMXPF71001(ASMXPF71001 asmxpf71001, XpilotFileStorageSupport storageSupport) {
        this.asmxpf71001 = asmxpf71001;
        this.storageSupport = storageSupport;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StandardResponse<FileResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "GENERAL") String bizCategory,
            @RequestParam(required = false) String description
    ) {
        System.out.println("★★★★★★★ [" + AC + "] upload START bizCategory=" + bizCategory
                + " fileName=" + (file != null ? file.getOriginalFilename() : null));
        FileResponse response = FileCDtoConverter.toResponse(
                asmxpf71001.upload(file, bizCategory, description), storageSupport);
        StandardResponse<FileResponse> result =
                StandardResponse.success("XPF-UPLOAD-001", "xpilotFileUpload", response);
        System.out.println("★★★★★★★ [" + AC + "] upload END fileId=" + (response != null ? response.fileId() : null));
        return result;
    }
}
