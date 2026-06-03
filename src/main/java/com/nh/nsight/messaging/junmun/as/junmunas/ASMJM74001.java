package com.nh.nsight.messaging.junmun.as.junmunas;

import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunBuildRequestCDTO;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunBuildResultCDTO;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunCDtoConverter;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunDefinitionCDTO;
import com.nh.nsight.messaging.junmun.dc.junmundc.DCJunmunDefinition;
import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;
import com.nh.nsight.messaging.junmun.util.JunmunBizException;
import com.nh.nsight.messaging.junmun.util.JunmunJsonEnvelopeBuilder;
import com.nh.nsight.messaging.junmun.util.JunmunPh1StandardCatalog;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ASMJM74001 {

    private final DCJunmunDefinition dcJunmunDefinition;

    public ASMJM74001(DCJunmunDefinition dcJunmunDefinition) {
        this.dcJunmunDefinition = dcJunmunDefinition;
    }

    public JunmunDefinitionCDTO defaults() {
        return JunmunCDtoConverter.defaultPh1Request();
    }

    public JunmunBuildResultCDTO build(String messageCode, JunmunBuildRequestCDTO request) {
        JunmunDefinitionDDTO definition = resolveDefinition(messageCode);
        String envelope = JunmunJsonEnvelopeBuilder.build(definition, request);
        List<String> errors = JunmunJsonEnvelopeBuilder.validate(definition.layoutJson(), envelope);
        JunmunBuildResultCDTO result = new JunmunBuildResultCDTO();
        result.setMessageCode(definition.messageCode());
        result.setEnvelopeJson(envelope);
        result.setValid(errors.isEmpty());
        result.setValidationErrors(errors);
        return result;
    }

    public JunmunBuildResultCDTO validate(String messageCode, String envelopeJson) {
        JunmunDefinitionDDTO definition = resolveDefinition(messageCode);
        List<String> errors = JunmunJsonEnvelopeBuilder.validate(definition.layoutJson(), envelopeJson);
        JunmunBuildResultCDTO result = new JunmunBuildResultCDTO();
        result.setMessageCode(definition.messageCode());
        result.setEnvelopeJson(envelopeJson);
        result.setValid(errors.isEmpty());
        result.setValidationErrors(errors);
        return result;
    }

    private JunmunDefinitionDDTO resolveDefinition(String messageCode) {
        if (messageCode == null || messageCode.isBlank()) {
            throw new JunmunBizException("전문코드는 필수입니다.");
        }
        JunmunDefinitionDDTO found = dcJunmunDefinition.get(messageCode.trim());
        if (found == null) {
            if (JunmunPh1StandardCatalog.DEFAULT_MESSAGE_CODE.equalsIgnoreCase(messageCode.trim())) {
                return JunmunCDtoConverter.toDomain(JunmunCDtoConverter.defaultPh1Request());
            }
            throw new JunmunBizException("전문 정의를 찾을 수 없습니다: " + messageCode);
        }
        return found;
    }
}
