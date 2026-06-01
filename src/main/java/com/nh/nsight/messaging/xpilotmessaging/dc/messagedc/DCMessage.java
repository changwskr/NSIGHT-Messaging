package com.nh.nsight.messaging.xpilotmessaging.dc.messagedc;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageDDTO;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageSearchDDTO;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.repository.MessageRepository;
import com.nh.nsight.messaging.xpilotmessaging.zcommonutil.MessageMapperUtil;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DCMessage implements IDCMessage {

    private final MessageRepository messageRepository;

    public DCMessage(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void createMessage(MessageDDTO messageDDTO) {
        XpmMessage entity = MessageMapperUtil.toEntity(messageDDTO);
        messageRepository.insert(entity);
        messageDDTO.setMessageId(entity.getMessageId());
    }

    @Override
    public MessageDDTO getMessage(Long messageId) {
        return messageRepository.findById(messageId)
                .map(MessageMapperUtil::toDDto)
                .orElse(null);
    }

    @Override
    public List<MessageDDTO> searchMessages(MessageSearchDDTO condition) {
        List<MessageDDTO> result = new ArrayList<>();
        for (XpmMessage message : messageRepository.findMessages(condition)) {
            result.add(MessageMapperUtil.toDDto(message));
        }
        return result;
    }

    @Override
    public long countMessages(MessageSearchDDTO condition) {
        return messageRepository.countMessages(condition);
    }

    @Override
    public MessageDDTO updateMessage(Long messageId, MessageDDTO messageDDTO) {
        messageDDTO.setMessageId(messageId);
        int updated = messageRepository.update(MessageMapperUtil.toEntity(messageDDTO));
        if (updated == 0) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "messageId=" + messageId);
        }
        return getMessage(messageId);
    }

    @Override
    public void deleteMessage(Long messageId) {
        int deleted = messageRepository.deleteById(messageId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "messageId=" + messageId);
        }
    }

    public boolean existsByCode(String messageCode) {
        return messageRepository.findByCode(messageCode).isPresent();
    }

    public MessageDDTO findByCode(String messageCode) {
        return messageRepository.findByCode(messageCode)
                .map(MessageMapperUtil::toDDto)
                .orElse(null);
    }
}
