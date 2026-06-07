package com.nh.nsight.messaging.zpilotfwk.order.dc.mapper;

import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.order.dc.entity.SpOrderEntity;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpOrderMapper {

    SpOrderEntity selectById(@Param("id") Long id);

    List<SpOrderEntity> selectAll(@Param("criteria") SpOrder7101BIZDDTO criteria);

    long countAll(@Param("criteria") SpOrder7101BIZDDTO criteria);

    int insert(SpOrderEntity entity);

    int update(SpOrderEntity entity);

    int deleteById(@Param("id") Long id);
}
