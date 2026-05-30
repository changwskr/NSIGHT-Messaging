package com.nh.nsight.messaging.message.service;

import com.nh.nsight.messaging.common.context.RequestContext;
import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.message.dao.MessageDao;
import com.nh.nsight.messaging.message.dto.MessageCreateRequest;
import com.nh.nsight.messaging.message.dto.MessageResponse;
import com.nh.nsight.messaging.message.dto.MessageSearchCondition;
import com.nh.nsight.messaging.message.rule.MessageRule;
import com.nh.nsight.messaging.message.thing.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final MessageDao messageDao;
    private final MessageRule messageRule;

    public MessageService(MessageDao messageDao, MessageRule messageRule) {
        this.messageDao = messageDao;
        this.messageRule = messageRule;
    }

    public MessageResponse createMessage(MessageCreateRequest request) {
        messageRule.validateCreate(request);
        messageDao.findByCode(request.messageCode()).ifPresent(existing -> {
            throw new BusinessException(ErrorCode.BIZ_DUPLICATE_MESSAGE_CODE, "messageCode=" + request.messageCode());
        });

        String userId = RequestContext.get().userId();
        Message message = Message.create(
                request.messageCode(),
                request.messageName(),
                request.messageType(),
                request.channelCode(),
                request.locale(),
                request.messageContent(),
                request.displayStartAt(),
                request.displayEndAt(),
                request.useYn(),
                userId
        );
        messageDao.insert(message);
        return MessageResponse.from(message);
    }

    public MessageResponse getMessage(Long messageId) {
        Message message = messageDao.findById(messageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BIZ_NO_DATA, "messageId=" + messageId));
        return MessageResponse.from(message);
    }

    public List<MessageResponse> searchMessages(MessageSearchCondition condition) {
        return messageDao.findMessages(condition).stream()
                .map(MessageResponse::from)
                .toList();
    }

    public long countMessages(MessageSearchCondition condition) {
        return messageDao.countMessages(condition);
    }
}
