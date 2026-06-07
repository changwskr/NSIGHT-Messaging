package com.nh.nsight.messaging.zpilotfwk.common.dc.util;

import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.common.dc.entity.SpCommonEntity;

/**
 * {@link SpCommon7001BIZDDTO} ??{@link SpCommonEntity} 변??
 */
public final class SpCommonDcMapperUtil {

    private SpCommonDcMapperUtil() {
    }

    public static SpCommonEntity toEntity(SpCommon7001BIZDDTO dto) {
        if (dto == null) {
            return null;
        }
        SpCommonEntity entity = new SpCommonEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setAge(dto.getAge());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    public static SpCommon7001BIZDDTO toBizDto(SpCommonEntity entity) {
        if (entity == null) {
            return null;
        }
        SpCommon7001BIZDDTO dto = new SpCommon7001BIZDDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAge(entity.getAge());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
