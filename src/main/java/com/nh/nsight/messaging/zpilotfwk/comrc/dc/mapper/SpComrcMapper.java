package com.nh.nsight.messaging.zpilotfwk.comrc.dc.mapper;

import com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto.SpComrc7201BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.comrc.dc.entity.SpComrcEntity;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpComrcMapper {

    SpComrcEntity selectById(@Param("id") Long id);

    List<SpComrcEntity> selectAll(@Param("criteria") SpComrc7201BIZDDTO criteria);

    long countAll(@Param("criteria") SpComrc7201BIZDDTO criteria);

    int insert(SpComrcEntity entity);

    int update(SpComrcEntity entity);

    int deleteById(@Param("id") Long id);
}
