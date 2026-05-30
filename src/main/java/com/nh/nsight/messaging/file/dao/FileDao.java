package com.nh.nsight.messaging.file.dao;

import com.nh.nsight.messaging.file.dto.FileSearchCondition;
import com.nh.nsight.messaging.file.mapper.FileMapper;
import com.nh.nsight.messaging.file.thing.FileDocument;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FileDao {
    private final FileMapper mapper;

    public FileDao(FileMapper mapper) {
        this.mapper = mapper;
    }

    public void insert(FileDocument document) {
        mapper.insertFile(document);
    }

    public Optional<FileDocument> findById(Long fileId) {
        return Optional.ofNullable(mapper.selectById(fileId));
    }

    public List<FileDocument> findFiles(FileSearchCondition condition) {
        return mapper.selectFiles(condition);
    }

    public long countFiles(FileSearchCondition condition) {
        return mapper.countFiles(condition);
    }

    public int updateUseYn(Long fileId, String useYn, String updatedBy) {
        return mapper.updateUseYn(fileId, useYn, updatedBy);
    }

    public int deleteById(Long fileId) {
        return mapper.deleteById(fileId);
    }
}
