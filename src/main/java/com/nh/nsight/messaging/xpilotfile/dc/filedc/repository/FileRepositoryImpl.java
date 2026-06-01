package com.nh.nsight.messaging.xpilotfile.dc.filedc.repository;

import com.nh.nsight.messaging.xpilotfile.dc.filedc.XpfFile;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileSearchDDTO;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.mapper.XpfFileMapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FileRepositoryImpl implements FileRepository {

    private final XpfFileMapper xpfFileMapper;

    public FileRepositoryImpl(XpfFileMapper xpfFileMapper) {
        this.xpfFileMapper = xpfFileMapper;
    }

    @Override
    public void insert(XpfFile file) {
        xpfFileMapper.insertFile(file);
    }

    @Override
    public Optional<XpfFile> findById(Long fileId) {
        return Optional.ofNullable(xpfFileMapper.selectById(fileId));
    }

    @Override
    public List<XpfFile> findFiles(FileSearchDDTO condition) {
        return xpfFileMapper.selectFiles(condition);
    }

    @Override
    public long countFiles(FileSearchDDTO condition) {
        return xpfFileMapper.countFiles(condition);
    }

    @Override
    public int updateUseYn(Long fileId, String useYn, String updatedBy) {
        return xpfFileMapper.updateUseYn(fileId, useYn, updatedBy);
    }

    @Override
    public int deleteById(Long fileId) {
        return xpfFileMapper.deleteById(fileId);
    }
}
