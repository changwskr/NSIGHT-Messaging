package com.nh.nsight.messaging.xpilot.as.pilotas;

import com.nh.nsight.messaging.xpilot.ac.pilotac.dto.PilotCDTO;
import com.nh.nsight.messaging.xpilot.ac.pilotac.dto.PilotCDtoConverter;
import com.nh.nsight.messaging.xpilot.dc.pilotdc.DCPilot;
import com.nh.nsight.messaging.xpilot.dc.pilotdc.dto.PilotDDTO;
import com.nh.nsight.messaging.xpilot.zcommonutil.XpilotBizException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Pilot 세션 Application Service.
 */
@Service
public class ASMXP71001 {

    private final DCPilot dcPilot;

    public ASMXP71001(DCPilot dcPilot) {
        this.dcPilot = dcPilot;
    }

    public PilotCDTO create(PilotCDTO pilotCDTO) {
        validateForCreate(pilotCDTO);
        if (pilotCDTO.getPilotId() == null || pilotCDTO.getPilotId().isBlank()) {
            pilotCDTO.setPilotId("PILOT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        PilotDDTO ddto = PilotCDtoConverter.toDDto(pilotCDTO);
        dcPilot.createPilot(ddto);
        PilotDDTO criteria = new PilotDDTO();
        criteria.setPilotId(ddto.getPilotId());
        return PilotCDtoConverter.toCDto(dcPilot.getPilot(criteria));
    }

    public PilotCDTO get(String pilotId) {
        PilotDDTO criteria = new PilotDDTO();
        criteria.setPilotId(pilotId);
        PilotDDTO found = dcPilot.getPilot(criteria);
        if (found == null) {
            throw new XpilotBizException("Pilot 세션을 찾을 수 없습니다: " + pilotId);
        }
        return PilotCDtoConverter.toCDto(found);
    }

    public List<PilotCDTO> list(PilotCDTO criteria) {
        return PilotCDtoConverter.toCDtoList(
                dcPilot.listPilots(PilotCDtoConverter.toDDto(criteria))
        );
    }

    private void validateForCreate(PilotCDTO pilotCDTO) {
        if (pilotCDTO == null || pilotCDTO.getPilotName() == null || pilotCDTO.getPilotName().isBlank()) {
            throw new XpilotBizException("pilotName은 필수입니다.");
        }
    }
}
