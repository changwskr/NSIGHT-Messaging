package com.nh.nsight.messaging.xpilotfile.dc.filedc;

import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileDDTO;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileSearchDDTO;

import java.util.List;

public interface IDCFile {

    void createFile(FileDDTO fileDDTO);

    FileDDTO getFile(Long fileId);

    List<FileDDTO> searchFiles(FileSearchDDTO condition);

    long countFiles(FileSearchDDTO condition);

    FileDDTO updateUseYn(Long fileId, String useYn, String updatedBy);

    void deleteFile(Long fileId);
}
