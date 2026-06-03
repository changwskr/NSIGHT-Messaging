package com.nh.nsight.messaging.junmun.as.junmunas;

import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunCDtoConverter;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunDefinitionCDTO;
import com.nh.nsight.messaging.junmun.dc.junmundc.DCJunmunDefinition;

import org.springframework.stereotype.Service;

@Service
public class ASMJM73001 {

    private final DCJunmunDefinition dcJunmunDefinition;

    public ASMJM73001(DCJunmunDefinition dcJunmunDefinition) {
        this.dcJunmunDefinition = dcJunmunDefinition;
    }

    public JunmunDefinitionCDTO update(JunmunDefinitionCDTO request) {
        dcJunmunDefinition.update(JunmunCDtoConverter.toDomain(request));
        return JunmunCDtoConverter.toCDto(dcJunmunDefinition.get(request.getMessageCode()));
    }

    public void delete(String messageCode) {
        dcJunmunDefinition.delete(messageCode);
    }
}
