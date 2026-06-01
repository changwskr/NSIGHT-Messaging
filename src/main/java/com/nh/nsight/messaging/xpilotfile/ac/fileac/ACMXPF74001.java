package com.nh.nsight.messaging.xpilotfile.ac.fileac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilotfile.as.fileas.ASMXPF74001;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xpilotfile/files")
public class ACMXPF74001 {

    private static final String AC = "ACMXPF74001";

    private final ASMXPF74001 asmxpf74001;

    public ACMXPF74001(ASMXPF74001 asmxpf74001) {
        this.asmxpf74001 = asmxpf74001;
    }

    @DeleteMapping("/{fileId}")
    public StandardResponse<Void> deleteFile(@PathVariable Long fileId) {
        System.out.println("★★★★★★★ [" + AC + "] deleteFile START fileId=" + fileId);
        asmxpf74001.delete(fileId);
        StandardResponse<Void> result = StandardResponse.success("XPF-DELETE-001", "xpilotFileDelete", null);
        System.out.println("★★★★★★★ [" + AC + "] deleteFile END fileId=" + fileId);
        return result;
    }
}
