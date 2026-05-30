package com.nh.nsight.messaging.message.dto;

import com.nh.nsight.messaging.message.thing.Message;

import java.time.LocalDateTime;

public record MessageResponse(
        Long messageId,
        String messageCode,
        String messageName,
        String messageType,
        String channelCode,
        String locale,
        String messageContent,
        LocalDateTime displayStartAt,
        LocalDateTime displayEndAt,
        String useYn,
        boolean activeNow,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getMessageId(),
                message.getMessageCode(),
                message.getMessageName(),
                message.getMessageType(),
                message.getChannelCode(),
                message.getLocale(),
                message.getMessageContent(),
                message.getDisplayStartAt(),
                message.getDisplayEndAt(),
                message.getUseYn(),
                message.isActiveNow(LocalDateTime.now()),
                message.getCreatedBy(),
                message.getCreatedAt(),
                message.getUpdatedBy(),
                message.getUpdatedAt()
        );
    }
}
