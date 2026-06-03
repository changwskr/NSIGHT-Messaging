package com.nh.nsight.messaging.junmun.dc.junmundc;

import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;

import java.util.List;

public interface IDCJunmunDefinition {

    JunmunDefinitionDDTO get(String messageCode);

    List<JunmunDefinitionDDTO> list(JunmunDefinitionDDTO criteria);

    void create(JunmunDefinitionDDTO dto);

    void update(JunmunDefinitionDDTO dto);

    void delete(String messageCode);
}
