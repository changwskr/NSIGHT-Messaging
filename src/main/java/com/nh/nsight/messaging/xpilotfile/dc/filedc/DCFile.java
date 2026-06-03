package com.nh.nsight.messaging.xpilotfile.dc.filedc;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileDDTO;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileSearchDDTO;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.repository.FileRepository;
import com.nh.nsight.messaging.xpilotfile.util.FileMapperUtil;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DCFile implements IDCFile {

    private static final String DC = "DCFile";

    private final FileRepository fileRepository;

    public DCFile(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public void createFile(FileDDTO fileDDTO) {
        System.out.println("★★★★★★★ [" + DC + "] createFile START originalName="
                + (fileDDTO != null ? fileDDTO.getOriginalName() : null));
        XpfFile entity = FileMapperUtil.toEntity(fileDDTO);
        fileRepository.insert(entity);
        fileDDTO.setFileId(entity.getFileId());
        System.out.println("★★★★★★★ [" + DC + "] createFile END fileId=" + entity.getFileId());
    }

    @Override
    public FileDDTO getFile(Long fileId) {
        System.out.println("★★★★★★★ [" + DC + "] getFile START fileId=" + fileId);
        FileDDTO result = fileRepository.findById(fileId)
                .map(FileMapperUtil::toDDto)
                .orElse(null);
        System.out.println("★★★★★★★ [" + DC + "] getFile END fileId=" + fileId);
        return result;
    }

    @Override
    public List<FileDDTO> searchFiles(FileSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + DC + "] searchFiles START");
        List<FileDDTO> result = new ArrayList<>();
        for (XpfFile file : fileRepository.findFiles(condition)) {
            result.add(FileMapperUtil.toDDto(file));
        }
        System.out.println("★★★★★★★ [" + DC + "] searchFiles END size=" + result.size());
        return result;
    }

    @Override
    public long countFiles(FileSearchDDTO condition) {
        System.out.println("★★★★★★★ [" + DC + "] countFiles START");
        long total = fileRepository.countFiles(condition);
        System.out.println("★★★★★★★ [" + DC + "] countFiles END total=" + total);
        return total;
    }

    @Override
    public FileDDTO updateUseYn(Long fileId, String useYn, String updatedBy) {
        System.out.println("★★★★★★★ [" + DC + "] updateUseYn START fileId=" + fileId + " useYn=" + useYn);
        int updated = fileRepository.updateUseYn(fileId, useYn, updatedBy);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "fileId=" + fileId);
        }
        FileDDTO result = getFile(fileId);
        System.out.println("★★★★★★★ [" + DC + "] updateUseYn END fileId=" + fileId);
        return result;
    }

    @Override
    public void deleteFile(Long fileId) {
        System.out.println("★★★★★★★ [" + DC + "] deleteFile START fileId=" + fileId);
        int deleted = fileRepository.deleteById(fileId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "fileId=" + fileId);
        }
        System.out.println("★★★★★★★ [" + DC + "] deleteFile END fileId=" + fileId);
    }
}
