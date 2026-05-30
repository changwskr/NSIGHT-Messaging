package com.nh.nsight.messaging.message.facade;

import com.nh.nsight.messaging.message.dto.MessageCreateRequest;
import com.nh.nsight.messaging.message.dto.MessageResponse;
import com.nh.nsight.messaging.message.dto.MessageSearchCondition;
import com.nh.nsight.messaging.message.service.MessageService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class MessageFacade {
    private final MessageService messageService;

    public MessageFacade(MessageService messageService) {
        this.messageService = messageService;
    }

    @Transactional(timeout = 5)
    public MessageResponse createMessage(MessageCreateRequest request) {
        return messageService.createMessage(request);
    }

    @Transactional(readOnly = true, timeout = 3)
    public MessageResponse getMessage(Long messageId) {
        return messageService.getMessage(messageId);
    }

    @Transactional(readOnly = true, timeout = 3)
    public List<MessageResponse> searchMessages(MessageSearchCondition condition) {
        return messageService.searchMessages(condition);
    }

    @Transactional(readOnly = true, timeout = 3)
    public long countMessages(MessageSearchCondition condition) {
        return messageService.countMessages(condition);
    }
}
