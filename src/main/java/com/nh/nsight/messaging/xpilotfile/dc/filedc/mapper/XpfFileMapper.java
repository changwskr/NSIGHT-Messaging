package com.nh.nsight.messaging.xpilotfile.dc.filedc.mapper;

import com.nh.nsight.messaging.xpilotfile.dc.filedc.XpfFile;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileSearchDDTO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XpfFileMapper {

    int insertFile(XpfFile file);

    XpfFile selectById(@Param("fileId") Long fileId);

    List<XpfFile> selectFiles(FileSearchDDTO condition);

    long countFiles(FileSearchDDTO condition);

    int updateUseYn(@Param("fileId") Long fileId, @Param("useYn") String useYn, @Param("updatedBy") String updatedBy);

    int deleteById(@Param("fileId") Long fileId);
}
