package com.nh.nsight.messaging.junmun.util;

import com.nh.nsight.messaging.junmun.dc.junmundc.JunmunDefinition;
import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;

import java.util.ArrayList;
import java.util.List;

public final class JunmunMapperUtil {

    private JunmunMapperUtil() {
    }

    public static JunmunDefinitionDDTO toDDto(JunmunDefinition entity) {
        if (entity == null) {
            return null;
        }
        return new JunmunDefinitionDDTO(
                entity.getDefinitionId(),
                entity.getMessageCode(),
                entity.getMessageName(),
                entity.getTransactionId(),
                entity.getServiceId(),
                entity.getDirection(),
                entity.getStandardVersion(),
                entity.getDocumentRef(),
                entity.getLayoutJson(),
                entity.getSampleJson(),
                entity.getDescription(),
                entity.getUseYn(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt()
        );
    }

    public static JunmunDefinition toEntity(JunmunDefinitionDDTO dto) {
        if (dto == null) {
            return null;
        }
        JunmunDefinition entity = new JunmunDefinition();
        entity.setDefinitionId(dto.definitionId());
        entity.setMessageCode(dto.messageCode());
        entity.setMessageName(dto.messageName());
        entity.setTransactionId(dto.transactionId());
        entity.setServiceId(dto.serviceId());
        entity.setDirection(dto.direction());
        entity.setStandardVersion(dto.standardVersion());
        entity.setDocumentRef(dto.documentRef());
        entity.setLayoutJson(dto.layoutJson());
        entity.setSampleJson(dto.sampleJson());
        entity.setDescription(dto.description());
        entity.setUseYn(dto.useYn());
        entity.setCreatedBy(dto.createdBy());
        entity.setCreatedAt(dto.createdAt());
        entity.setUpdatedBy(dto.updatedBy());
        entity.setUpdatedAt(dto.updatedAt());
        return entity;
    }

    public static List<JunmunDefinitionDDTO> toDDtoList(List<JunmunDefinition> entities) {
        List<JunmunDefinitionDDTO> list = new ArrayList<>();
        if (entities == null) {
            return list;
        }
        for (JunmunDefinition entity : entities) {
            list.add(toDDto(entity));
        }
        return list;
    }
}
