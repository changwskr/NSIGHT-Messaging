package com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto;

import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.dto.UserProfileDDTO;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CDTO(AC·AS) ↔ DDTO(DC) 변환.
 */
public final class UserProfileCDtoConverter {

    private UserProfileCDtoConverter() {
    }

    public static UserProfileDDTO toDDto(UserProfileCDTO source) {
        if (source == null) {
            return null;
        }
        UserProfileDDTO target = new UserProfileDDTO();
        target.setUserId(source.getUserId());
        target.setUserName(source.getUserName());
        target.setEmail(source.getEmail());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setRoleCode(source.getRoleCode());
        target.setStatus(source.getStatus());
        target.setCreatedDate(parseDate(source.getCreatedDate()));
        target.setUpdatedDate(parseDate(source.getUpdatedDate()));
        return target;
    }

    public static UserProfileCDTO toCDto(UserProfileDDTO source) {
        if (source == null) {
            return null;
        }
        UserProfileCDTO target = new UserProfileCDTO();
        target.setUserId(source.getUserId());
        target.setUserName(source.getUserName());
        target.setEmail(source.getEmail());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setRoleCode(source.getRoleCode());
        target.setStatus(source.getStatus());
        target.setCreatedDate(formatDate(source.getCreatedDate()));
        target.setUpdatedDate(formatDate(source.getUpdatedDate()));
        return target;
    }

    public static List<UserProfileCDTO> toCDtoList(List<UserProfileDDTO> sources) {
        List<UserProfileCDTO> result = new ArrayList<>();
        if (sources == null) {
            return result;
        }
        for (UserProfileDDTO source : sources) {
            result.add(toCDto(source));
        }
        return result;
    }

    private static Date parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Date.from(Instant.parse(value.trim()));
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private static String formatDate(Date value) {
        return value == null ? null : value.toInstant().toString();
    }
}
