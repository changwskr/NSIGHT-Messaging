package com.nh.nsight.messaging.xpilotfile.as.fileas;

import com.nh.nsight.messaging.common.context.RequestContext;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileCDTO;
import com.nh.nsight.messaging.xpilotfile.ac.fileac.dto.FileCDtoConverter;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.DCFile;
import com.nh.nsight.messaging.xpilotfile.util.XpilotFileRule;
import com.nh.nsight.messaging.xpilotfile.util.XpilotFileStorageSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ASMXPF73001 {

    private static final String AS = "ASMXPF73001";

    private final DCFile dcFile;
    private final XpilotFileRule fileRule;
    private final XpilotFileStorageSupport storageSupport;

    public ASMXPF73001(DCFile dcFile, XpilotFileRule fileRule, XpilotFileStorageSupport storageSupport) {
        this.dcFile = dcFile;
        this.fileRule = fileRule;
        this.storageSupport = storageSupport;
    }

    @Transactional(timeout = 5)
    public FileCDTO updateUseYn(Long fileId, String useYn) {
        System.out.println("★★★★★★★ [" + AS + "] updateUseYn START fileId=" + fileId + " useYn=" + useYn);
        fileRule.validateUseYn(useYn);
        String userId = RequestContext.get().userId();
        FileCDTO result = FileCDtoConverter.toCDto(dcFile.updateUseYn(fileId, useYn, userId));
        FileCDTO enriched = FileCDtoConverter.enrichPaths(result, storageSupport);
        System.out.println("★★★★★★★ [" + AS + "] updateUseYn END fileId=" + fileId);
        return enriched;
    }
}
