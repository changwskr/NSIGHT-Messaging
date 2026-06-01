package com.nh.nsight.messaging.xpilotfile.as.fileas;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.DCFile;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileDDTO;
import com.nh.nsight.messaging.xpilotfile.zcommonutil.XpilotFileStorageSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;

@Service
public class ASMXPF74001 {

    private static final String AS = "ASMXPF74001";

    private final DCFile dcFile;
    private final XpilotFileStorageSupport storageSupport;

    public ASMXPF74001(DCFile dcFile, XpilotFileStorageSupport storageSupport) {
        this.dcFile = dcFile;
        this.storageSupport = storageSupport;
    }

    @Transactional(timeout = 5)
    public void delete(Long fileId) {
        System.out.println("★★★★★★★ [" + AS + "] delete START fileId=" + fileId);
        FileDDTO ddto = dcFile.getFile(fileId);
        if (ddto == null) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "fileId=" + fileId);
        }
        try {
            Files.deleteIfExists(storageSupport.resolvePhysicalPath(ddto));
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.SYS_UNKNOWN, "파일 삭제 실패: " + ex.getMessage());
        }
        dcFile.deleteFile(fileId);
        System.out.println("★★★★★★★ [" + AS + "] delete END fileId=" + fileId);
    }
}
