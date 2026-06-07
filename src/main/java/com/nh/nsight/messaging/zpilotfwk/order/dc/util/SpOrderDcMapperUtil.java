package com.nh.nsight.messaging.zpilotfwk.order.dc.util;

import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.order.dc.entity.SpOrderEntity;

public final class SpOrderDcMapperUtil {

    private SpOrderDcMapperUtil() {
    }

    public static SpOrderEntity toEntity(SpOrder7101BIZDDTO dto) {
        if (dto == null) {
            return null;
        }
        SpOrderEntity entity = new SpOrderEntity();
        entity.setId(dto.getId());
        entity.setOrderNo(dto.getOrderNo());
        entity.setCustomerName(dto.getCustomerName());
        entity.setAmount(dto.getAmount());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    public static SpOrder7101BIZDDTO toBizDto(SpOrderEntity entity) {
        if (entity == null) {
            return null;
        }
        SpOrder7101BIZDDTO dto = new SpOrder7101BIZDDTO();
        dto.setId(entity.getId());
        dto.setOrderNo(entity.getOrderNo());
        dto.setCustomerName(entity.getCustomerName());
        dto.setAmount(entity.getAmount());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
