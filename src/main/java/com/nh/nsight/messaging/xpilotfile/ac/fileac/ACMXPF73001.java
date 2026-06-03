package com.nh.nsight.messaging.xpilotfile.ac.fileac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.file.dto.FileResponse;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileCDtoConverter;
import com.nh.nsight.messaging.xpilotfile.as.fileas.ASMXPF73001;
import com.nh.nsight.messaging.xpilotfile.util.XpilotFileStorageSupport;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xpilotfile/files")
public class ACMXPF73001 {

    private static final String AC = "ACMXPF73001";

    private final ASMXPF73001 asmxpf73001;
    private final XpilotFileStorageSupport storageSupport;

    public ACMXPF73001(ASMXPF73001 asmxpf73001, XpilotFileStorageSupport storageSupport) {
        this.asmxpf73001 = asmxpf73001;
        this.storageSupport = storageSupport;
    }

    @PutMapping("/{fileId}/use-yn")
    public StandardResponse<FileResponse> updateUseYn(
            @PathVariable Long fileId,
            @RequestParam String useYn) {
        System.out.println("★★★★★★★ [" + AC + "] updateUseYn START fileId=" + fileId + " useYn=" + useYn);
        FileResponse response = FileCDtoConverter.toResponse(asmxpf73001.updateUseYn(fileId, useYn), storageSupport);
        StandardResponse<FileResponse> result = StandardResponse.success("XPF-UPDATE-001", "xpilotFileUpdate",
                response);
        System.out.println("★★★★★★★ [" + AC + "] updateUseYn END fileId=" + fileId);
        return result;
    }
}
