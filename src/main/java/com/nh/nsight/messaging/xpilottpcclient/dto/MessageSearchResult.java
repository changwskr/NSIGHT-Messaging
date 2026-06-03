package com.nh.nsight.messaging.xpilottpcclient.dto;

import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDTO;

import java.util.List;

/** GET /api/xpilotmessaging/messages 목록 응답. */
public record MessageSearchResult(
        List<MessageCDTO> messages,
        int pageNo,
        int pageSize,
        long totalCount
) {
}
