package com.nh.nsight.messaging.junmun.dc.junmundc.mapper;

import com.nh.nsight.messaging.junmun.dc.junmundc.JunmunDefinition;
import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JunmunDefinitionMapper {

    JunmunDefinition selectByMessageCode(@Param("messageCode") String messageCode);

    List<JunmunDefinition> selectAll(@Param("criteria") JunmunDefinitionDDTO criteria);

    int countByMessageCode(@Param("messageCode") String messageCode);

    int insert(JunmunDefinition definition);

    int update(JunmunDefinition definition);

    int deleteByMessageCode(@Param("messageCode") String messageCode);
}
