package com.nh.nsight.messaging.junmun.dc.junmundc.repository;

import com.nh.nsight.messaging.junmun.dc.junmundc.JunmunDefinition;
import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;

import java.util.List;

public interface JunmunDefinitionRepository {

    JunmunDefinition findByMessageCode(String messageCode);

    List<JunmunDefinition> findAll(JunmunDefinitionDDTO criteria);

    boolean existsByMessageCode(String messageCode);

    int insert(JunmunDefinition definition);

    int update(JunmunDefinition definition);

    int deleteByMessageCode(String messageCode);
}
