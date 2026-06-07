package com.nh.nsight.messaging.zpilotfwk.comrc.dc.util;

import com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto.SpComrc7201BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.comrc.dc.entity.SpComrcEntity;
import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.order.dc.entity.SpOrderEntity;

public final class SpComrcDcMapperUtil {

    private SpComrcDcMapperUtil() {
    }

    public static SpComrcEntity toEntity(SpComrc7201BIZDDTO dto) {
        if (dto == null) {
            return null;
        }
        SpComrcEntity entity = new SpComrcEntity();
        entity.setId(dto.getId());
        entity.setOrderNo(dto.getOrderNo());
        entity.setCustomerName(dto.getCustomerName());
        entity.setAmount(dto.getAmount());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    public static SpComrc7201BIZDDTO toBizDto(SpComrcEntity entity) {
        if (entity == null) {
            return null;
        }
        SpComrc7201BIZDDTO dto = new SpComrc7201BIZDDTO();
        dto.setId(entity.getId());
        dto.setOrderNo(entity.getOrderNo());
        dto.setCustomerName(entity.getCustomerName());
        dto.setAmount(entity.getAmount());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static SpOrder7101BIZDDTO toOrderDto(SpComrc7201BIZDDTO dto) {
        if (dto == null) {
            return null;
        }
        SpOrder7101BIZDDTO order = new SpOrder7101BIZDDTO();
        order.setId(dto.getId());
        order.setOrderNo(dto.getOrderNo());
        order.setCustomerName(dto.getCustomerName());
        order.setAmount(dto.getAmount());
        order.setStatus(dto.getStatus());
        order.setCreatedAt(dto.getCreatedAt());
        order.setUpdatedAt(dto.getUpdatedAt());
        order.setPageNo(dto.getPageNo());
        order.setPageSize(dto.getPageSize());
        return order;
    }

    public static SpComrc7201BIZDDTO fromOrderDto(SpOrder7101BIZDDTO dto) {
        if (dto == null) {
            return null;
        }
        SpComrc7201BIZDDTO comrc = new SpComrc7201BIZDDTO();
        comrc.setId(dto.getId());
        comrc.setOrderNo(dto.getOrderNo());
        comrc.setCustomerName(dto.getCustomerName());
        comrc.setAmount(dto.getAmount());
        comrc.setStatus(dto.getStatus());
        comrc.setCreatedAt(dto.getCreatedAt());
        comrc.setUpdatedAt(dto.getUpdatedAt());
        comrc.setPageNo(dto.getPageNo());
        comrc.setPageSize(dto.getPageSize());
        return comrc;
    }

    public static SpOrderEntity toOrderEntity(SpComrcEntity entity) {
        if (entity == null) {
            return null;
        }
        SpOrderEntity order = new SpOrderEntity();
        order.setId(entity.getId());
        order.setOrderNo(entity.getOrderNo());
        order.setCustomerName(entity.getCustomerName());
        order.setAmount(entity.getAmount());
        order.setStatus(entity.getStatus());
        order.setCreatedAt(entity.getCreatedAt());
        order.setUpdatedAt(entity.getUpdatedAt());
        return order;
    }

    public static SpComrcEntity fromOrderEntity(SpOrderEntity entity) {
        if (entity == null) {
            return null;
        }
        SpComrcEntity comrc = new SpComrcEntity();
        comrc.setId(entity.getId());
        comrc.setOrderNo(entity.getOrderNo());
        comrc.setCustomerName(entity.getCustomerName());
        comrc.setAmount(entity.getAmount());
        comrc.setStatus(entity.getStatus());
        comrc.setCreatedAt(entity.getCreatedAt());
        comrc.setUpdatedAt(entity.getUpdatedAt());
        return comrc;
    }
}
