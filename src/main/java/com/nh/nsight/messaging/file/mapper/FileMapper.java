package com.nh.nsight.messaging.file.mapper;

import com.nh.nsight.messaging.file.dto.FileSearchCondition;
import com.nh.nsight.messaging.file.thing.FileDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileMapper {
    int insertFile(FileDocument document);
    FileDocument selectById(@Param("fileId") Long fileId);
    List<FileDocument> selectFiles(FileSearchCondition condition);
    long countFiles(FileSearchCondition condition);
    int updateUseYn(@Param("fileId") Long fileId, @Param("useYn") String useYn, @Param("updatedBy") String updatedBy);
    int deleteById(@Param("fileId") Long fileId);
}
