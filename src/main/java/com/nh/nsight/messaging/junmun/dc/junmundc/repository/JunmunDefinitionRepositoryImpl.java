package com.nh.nsight.messaging.junmun.dc.junmundc.repository;

import com.nh.nsight.messaging.junmun.dc.junmundc.JunmunDefinition;
import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;
import com.nh.nsight.messaging.junmun.dc.junmundc.mapper.JunmunDefinitionMapper;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JunmunDefinitionRepositoryImpl implements JunmunDefinitionRepository {

    private final JunmunDefinitionMapper mapper;

    public JunmunDefinitionRepositoryImpl(JunmunDefinitionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public JunmunDefinition findByMessageCode(String messageCode) {
        return mapper.selectByMessageCode(messageCode);
    }

    @Override
    public List<JunmunDefinition> findAll(JunmunDefinitionDDTO criteria) {
        return mapper.selectAll(criteria);
    }

    @Override
    public boolean existsByMessageCode(String messageCode) {
        return mapper.countByMessageCode(messageCode) > 0;
    }

    @Override
    public int insert(JunmunDefinition definition) {
        return mapper.insert(definition);
    }

    @Override
    public int update(JunmunDefinition definition) {
        return mapper.update(definition);
    }

    @Override
    public int deleteByMessageCode(String messageCode) {
        return mapper.deleteByMessageCode(messageCode);
    }
}
