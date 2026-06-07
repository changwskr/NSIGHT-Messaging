package com.nh.nsight.messaging.zpilotfwk.common.dc.mapper;

import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.common.dc.entity.SpCommonEntity;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpCommonMapper {

    SpCommonEntity selectById(@Param("id") Long id);

    List<SpCommonEntity> selectAll(@Param("criteria") SpCommon7001BIZDDTO criteria);

    long countAll(@Param("criteria") SpCommon7001BIZDDTO criteria);

    int insert(SpCommonEntity entity);

    int update(SpCommonEntity entity);

    int deleteById(@Param("id") Long id);
}
