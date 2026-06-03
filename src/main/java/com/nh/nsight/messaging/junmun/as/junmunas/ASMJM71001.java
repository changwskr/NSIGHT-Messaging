package com.nh.nsight.messaging.junmun.as.junmunas;

import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunCDtoConverter;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunDefinitionCDTO;
import com.nh.nsight.messaging.junmun.dc.junmundc.DCJunmunDefinition;

import org.springframework.stereotype.Service;

@Service
public class ASMJM71001 {

    private final DCJunmunDefinition dcJunmunDefinition;

    public ASMJM71001(DCJunmunDefinition dcJunmunDefinition) {
        this.dcJunmunDefinition = dcJunmunDefinition;
    }

    public JunmunDefinitionCDTO create(JunmunDefinitionCDTO request) {
        dcJunmunDefinition.create(JunmunCDtoConverter.toDomain(request));
        return JunmunCDtoConverter.toCDto(dcJunmunDefinition.get(request.getMessageCode()));
    }
}
