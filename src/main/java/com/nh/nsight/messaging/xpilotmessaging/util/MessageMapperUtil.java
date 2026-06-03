package com.nh.nsight.messaging.xpilotmessaging.util;

import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.XpmMessage;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageDDTO;

public final class MessageMapperUtil {

    private MessageMapperUtil() {
    }

    public static MessageDDTO toDDto(XpmMessage entity) {
        if (entity == null) {
            return null;
        }
        MessageDDTO dto = new MessageDDTO();
        dto.setMessageId(entity.getMessageId());
        dto.setMessageCode(entity.getMessageCode());
        dto.setMessageName(entity.getMessageName());
        dto.setMessageType(entity.getMessageType());
        dto.setChannelCode(entity.getChannelCode());
        dto.setLocale(entity.getLocale());
        dto.setMessageContent(entity.getMessageContent());
        dto.setDisplayStartAt(entity.getDisplayStartAt());
        dto.setDisplayEndAt(entity.getDisplayEndAt());
        dto.setUseYn(entity.getUseYn());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static XpmMessage toEntity(MessageDDTO dto) {
        if (dto == null) {
            return null;
        }
        XpmMessage entity = new XpmMessage();
        entity.setMessageId(dto.getMessageId());
        entity.setMessageCode(dto.getMessageCode());
        entity.setMessageName(dto.getMessageName());
        entity.setMessageType(dto.getMessageType());
        entity.setChannelCode(dto.getChannelCode());
        entity.setLocale(dto.getLocale());
        entity.setMessageContent(dto.getMessageContent());
        entity.setDisplayStartAt(dto.getDisplayStartAt());
        entity.setDisplayEndAt(dto.getDisplayEndAt());
        entity.setUseYn(dto.getUseYn());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
