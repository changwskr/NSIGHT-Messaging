package com.nh.nsight.messaging.xpilottpcclient.util;

import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDTO;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCreateCDTO;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageUpdateCDTO;

/**
 * xpilotmessaging CDTO ↔ TpcUtil 요청 DTO 변환.
 */
public final class TpcMessageMapper {

    private TpcMessageMapper() {
    }

    public static MessageCreateCDTO toCreateRequest(MessageCDTO source) {
        if (source == null) {
            return null;
        }
        MessageCreateCDTO target = new MessageCreateCDTO();
        target.setMessageCode(source.getMessageCode());
        target.setMessageName(source.getMessageName());
        target.setMessageType(source.getMessageType());
        target.setChannelCode(source.getChannelCode());
        target.setLocale(source.getLocale());
        target.setMessageContent(source.getMessageContent());
        target.setDisplayStartAt(source.getDisplayStartAt());
        target.setDisplayEndAt(source.getDisplayEndAt());
        target.setUseYn(source.getUseYn() != null ? source.getUseYn() : "Y");
        return target;
    }

    public static MessageUpdateCDTO toUpdateRequest(MessageCDTO source) {
        if (source == null) {
            return null;
        }
        MessageUpdateCDTO target = new MessageUpdateCDTO();
        target.setMessageCode(source.getMessageCode());
        target.setMessageName(source.getMessageName());
        target.setMessageType(source.getMessageType());
        target.setChannelCode(source.getChannelCode());
        target.setLocale(source.getLocale());
        target.setMessageContent(source.getMessageContent());
        target.setDisplayStartAt(source.getDisplayStartAt());
        target.setDisplayEndAt(source.getDisplayEndAt());
        target.setUseYn(source.getUseYn() != null ? source.getUseYn() : "Y");
        return target;
    }
}
