package com.nh.nsight.messaging.xpilot.ac.pilotac.dto;

import com.nh.nsight.messaging.xpilot.dc.pilotdc.dto.PilotDDTO;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class PilotCDtoConverter {

    private PilotCDtoConverter() {
    }

    public static PilotDDTO toDDto(PilotCDTO source) {
        if (source == null) {
            return null;
        }
        PilotDDTO target = new PilotDDTO();
        target.setPilotId(source.getPilotId());
        target.setPilotName(source.getPilotName());
        target.setTargetModule(source.getTargetModule());
        target.setSourceStructure(source.getSourceStructure());
        target.setTargetStructure(source.getTargetStructure());
        target.setStatus(source.getStatus());
        target.setEnvRunId(source.getEnvRunId());
        target.setNote(source.getNote());
        target.setCreatedDate(parseDate(source.getCreatedDate()));
        target.setUpdatedDate(parseDate(source.getUpdatedDate()));
        return target;
    }

    public static PilotCDTO toCDto(PilotDDTO source) {
        if (source == null) {
            return null;
        }
        PilotCDTO target = new PilotCDTO();
        target.setPilotId(source.getPilotId());
        target.setPilotName(source.getPilotName());
        target.setTargetModule(source.getTargetModule());
        target.setSourceStructure(source.getSourceStructure());
        target.setTargetStructure(source.getTargetStructure());
        target.setStatus(source.getStatus());
        target.setEnvRunId(source.getEnvRunId());
        target.setNote(source.getNote());
        target.setCreatedDate(formatDate(source.getCreatedDate()));
        target.setUpdatedDate(formatDate(source.getUpdatedDate()));
        return target;
    }

    public static List<PilotCDTO> toCDtoList(List<PilotDDTO> sources) {
        List<PilotCDTO> list = new ArrayList<>();
        if (sources == null) {
            return list;
        }
        for (PilotDDTO source : sources) {
            list.add(toCDto(source));
        }
        return list;
    }

    private static Date parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Date.from(Instant.parse(value));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static String formatDate(Date value) {
        return value == null ? null : value.toInstant().toString();
    }
}
