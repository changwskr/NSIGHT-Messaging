package com.nh.nsight.messaging.xpilotstyleguide.util;

import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.UserProfile;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.dto.UserProfileDDTO;

public final class UserProfileMapperUtil {

    private UserProfileMapperUtil() {
    }

    public static UserProfile toEntity(UserProfileDDTO source) {
        if (source == null) {
            return null;
        }
        UserProfile target = new UserProfile();
        copyFields(source, target);
        return target;
    }

    public static UserProfileDDTO toDDto(UserProfile source) {
        if (source == null) {
            return null;
        }
        UserProfileDDTO target = new UserProfileDDTO();
        copyFields(source, target);
        return target;
    }

    private static void copyFields(UserProfileDDTO source, UserProfile target) {
        target.setUserId(source.getUserId());
        target.setUserName(source.getUserName());
        target.setEmail(source.getEmail());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setRoleCode(source.getRoleCode());
        target.setStatus(source.getStatus());
        target.setCreatedDate(source.getCreatedDate());
        target.setUpdatedDate(source.getUpdatedDate());
    }

    private static void copyFields(UserProfile source, UserProfileDDTO target) {
        target.setUserId(source.getUserId());
        target.setUserName(source.getUserName());
        target.setEmail(source.getEmail());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setRoleCode(source.getRoleCode());
        target.setStatus(source.getStatus());
        target.setCreatedDate(source.getCreatedDate());
        target.setUpdatedDate(source.getUpdatedDate());
    }
}
