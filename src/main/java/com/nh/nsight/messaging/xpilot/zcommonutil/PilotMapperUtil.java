package com.nh.nsight.messaging.xpilot.zcommonutil;

import com.nh.nsight.messaging.xpilot.dc.pilotdc.Pilot;
import com.nh.nsight.messaging.xpilot.dc.pilotdc.dto.PilotDDTO;

public final class PilotMapperUtil {

    private PilotMapperUtil() {
    }

    public static PilotDDTO toDDto(Pilot entity) {
        if (entity == null) {
            return null;
        }
        PilotDDTO dto = new PilotDDTO();
        dto.setPilotId(entity.getPilotId());
        dto.setPilotName(entity.getPilotName());
        dto.setTargetModule(entity.getTargetModule());
        dto.setSourceStructure(entity.getSourceStructure());
        dto.setTargetStructure(entity.getTargetStructure());
        dto.setStatus(entity.getStatus());
        dto.setEnvRunId(entity.getEnvRunId());
        dto.setNote(entity.getNote());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        return dto;
    }

    public static Pilot toEntity(PilotDDTO dto) {
        if (dto == null) {
            return null;
        }
        Pilot entity = new Pilot();
        entity.setPilotId(dto.getPilotId());
        entity.setPilotName(dto.getPilotName());
        entity.setTargetModule(dto.getTargetModule());
        entity.setSourceStructure(dto.getSourceStructure());
        entity.setTargetStructure(dto.getTargetStructure());
        entity.setStatus(dto.getStatus());
        entity.setEnvRunId(dto.getEnvRunId());
        entity.setNote(dto.getNote());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setUpdatedDate(dto.getUpdatedDate());
        return entity;
    }
}
