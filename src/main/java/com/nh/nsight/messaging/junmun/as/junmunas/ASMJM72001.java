package com.nh.nsight.messaging.junmun.as.junmunas;

import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunCDtoConverter;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunDefinitionCDTO;
import com.nh.nsight.messaging.junmun.dc.junmundc.DCJunmunDefinition;
import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;
import com.nh.nsight.messaging.junmun.util.JunmunBizException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ASMJM72001 {

    private final DCJunmunDefinition dcJunmunDefinition;

    public ASMJM72001(DCJunmunDefinition dcJunmunDefinition) {
        this.dcJunmunDefinition = dcJunmunDefinition;
    }

    public JunmunDefinitionCDTO get(String messageCode) {
        JunmunDefinitionDDTO found = dcJunmunDefinition.get(messageCode);
        if (found == null) {
            throw new JunmunBizException("전문 정의를 찾을 수 없습니다: " + messageCode);
        }
        return JunmunCDtoConverter.toCDto(found);
    }

    public List<JunmunDefinitionCDTO> list(JunmunDefinitionCDTO criteria) {
        JunmunDefinitionDDTO domainCriteria = null;
        if (criteria != null && hasSearchCriteria(criteria)) {
            domainCriteria = JunmunCDtoConverter.toDomain(criteria);
        }
        return JunmunCDtoConverter.toCDtoList(dcJunmunDefinition.list(domainCriteria));
    }

    private boolean hasSearchCriteria(JunmunDefinitionCDTO criteria) {
        return (criteria.getMessageCode() != null && !criteria.getMessageCode().isBlank())
                || (criteria.getMessageName() != null && !criteria.getMessageName().isBlank())
                || (criteria.getDirection() != null && !criteria.getDirection().isBlank())
                || (criteria.getUseYn() != null && !criteria.getUseYn().isBlank());
    }
}
