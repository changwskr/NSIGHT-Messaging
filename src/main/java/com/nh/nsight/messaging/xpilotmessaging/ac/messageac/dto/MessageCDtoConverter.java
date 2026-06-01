package com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto;

import com.nh.nsight.messaging.message.dto.MessageResponse;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.XpmMessage;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageDDTO;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageSearchDDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class MessageCDtoConverter {

    private MessageCDtoConverter() {
    }

    public static MessageDDTO toDDto(MessageCreateCDTO source) {
        if (source == null) {
            return null;
        }
        MessageDDTO target = new MessageDDTO();
        target.setMessageCode(source.getMessageCode());
        target.setMessageName(source.getMessageName());
        target.setMessageType(source.getMessageType());
        target.setChannelCode(source.getChannelCode());
        target.setLocale(source.getLocale());
        target.setMessageContent(source.getMessageContent());
        target.setDisplayStartAt(source.getDisplayStartAt());
        target.setDisplayEndAt(source.getDisplayEndAt());
        target.setUseYn(source.getUseYn());
        return target;
    }

    public static MessageDDTO toDDto(MessageUpdateCDTO source) {
        if (source == null) {
            return null;
        }
        MessageDDTO target = new MessageDDTO();
        target.setMessageCode(source.getMessageCode());
        target.setMessageName(source.getMessageName());
        target.setMessageType(source.getMessageType());
        target.setChannelCode(source.getChannelCode());
        target.setLocale(source.getLocale());
        target.setMessageContent(source.getMessageContent());
        target.setDisplayStartAt(source.getDisplayStartAt());
        target.setDisplayEndAt(source.getDisplayEndAt());
        target.setUseYn(source.getUseYn());
        return target;
    }

    public static MessageSearchDDTO toSearchDDto(MessageSearchCDTO source) {
        MessageSearchDDTO target = new MessageSearchDDTO();
        if (source == null) {
            return target;
        }
        target.setMessageType(source.getMessageType());
        target.setChannelCode(source.getChannelCode());
        target.setUseYn(source.getUseYn());
        target.setPageNo(source.getPageNo());
        target.setPageSize(source.getPageSize());
        return target;
    }

    public static MessageCDTO toCDto(MessageDDTO source) {
        if (source == null) {
            return null;
        }
        MessageCDTO target = new MessageCDTO();
        target.setMessageId(source.getMessageId());
        target.setMessageCode(source.getMessageCode());
        target.setMessageName(source.getMessageName());
        target.setMessageType(source.getMessageType());
        target.setChannelCode(source.getChannelCode());
        target.setLocale(source.getLocale());
        target.setMessageContent(source.getMessageContent());
        target.setDisplayStartAt(source.getDisplayStartAt());
        target.setDisplayEndAt(source.getDisplayEndAt());
        target.setUseYn(source.getUseYn());
        target.setCreatedBy(source.getCreatedBy());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setUpdatedAt(source.getUpdatedAt());
        XpmMessage temp = new XpmMessage();
        temp.setUseYn(source.getUseYn());
        temp.setDisplayStartAt(source.getDisplayStartAt());
        temp.setDisplayEndAt(source.getDisplayEndAt());
        target.setActiveNow(temp.isActiveNow(LocalDateTime.now()));
        return target;
    }

    public static List<MessageCDTO> toCDtoList(List<MessageDDTO> sources) {
        List<MessageCDTO> list = new ArrayList<>();
        if (sources == null) {
            return list;
        }
        for (MessageDDTO source : sources) {
            list.add(toCDto(source));
        }
        return list;
    }

    public static MessageResponse toResponse(MessageCDTO cdto) {
        if (cdto == null) {
            return null;
        }
        return new MessageResponse(
                cdto.getMessageId(),
                cdto.getMessageCode(),
                cdto.getMessageName(),
                cdto.getMessageType(),
                cdto.getChannelCode(),
                cdto.getLocale(),
                cdto.getMessageContent(),
                cdto.getDisplayStartAt(),
                cdto.getDisplayEndAt(),
                cdto.getUseYn(),
                Boolean.TRUE.equals(cdto.getActiveNow()),
                cdto.getCreatedBy(),
                cdto.getCreatedAt(),
                cdto.getUpdatedBy(),
                cdto.getUpdatedAt()
        );
    }

    public static List<MessageResponse> toResponseList(List<MessageCDTO> cdtoList) {
        return cdtoList.stream().map(MessageCDtoConverter::toResponse).toList();
    }
}
