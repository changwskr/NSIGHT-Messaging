package com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.repository;

import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.XpmMessage;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageSearchDDTO;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.mapper.XpmMessageMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    private final XpmMessageMapper xpmMessageMapper;

    public MessageRepositoryImpl(XpmMessageMapper xpmMessageMapper) {
        this.xpmMessageMapper = xpmMessageMapper;
    }

    @Override
    public void insert(XpmMessage message) {
        xpmMessageMapper.insertMessage(message);
    }

    @Override
    public Optional<XpmMessage> findById(Long messageId) {
        return Optional.ofNullable(xpmMessageMapper.selectById(messageId));
    }

    @Override
    public Optional<XpmMessage> findByCode(String messageCode) {
        return Optional.ofNullable(xpmMessageMapper.selectByCode(messageCode));
    }

    @Override
    public List<XpmMessage> findMessages(MessageSearchDDTO condition) {
        return xpmMessageMapper.selectMessages(condition);
    }

    @Override
    public long countMessages(MessageSearchDDTO condition) {
        return xpmMessageMapper.countMessages(condition);
    }

    @Override
    public int update(XpmMessage message) {
        return xpmMessageMapper.updateMessage(message);
    }

    @Override
    public int deleteById(Long messageId) {
        return xpmMessageMapper.deleteById(messageId);
    }
}
