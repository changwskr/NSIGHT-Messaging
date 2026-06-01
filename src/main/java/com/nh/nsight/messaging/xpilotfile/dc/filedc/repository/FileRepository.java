package com.nh.nsight.messaging.xpilotfile.dc.filedc.repository;

import com.nh.nsight.messaging.xpilotfile.dc.filedc.XpfFile;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileSearchDDTO;

import java.util.List;
import java.util.Optional;

public interface FileRepository {

    void insert(XpfFile file);

    Optional<XpfFile> findById(Long fileId);

    List<XpfFile> findFiles(FileSearchDDTO condition);

    long countFiles(FileSearchDDTO condition);

    int updateUseYn(Long fileId, String useYn, String updatedBy);

    int deleteById(Long fileId);
}
