package com.nh.nsight.messaging.message.dao;

import com.nh.nsight.messaging.message.dto.MessageSearchCondition;
import com.nh.nsight.messaging.message.mapper.MessageMapper;
import com.nh.nsight.messaging.message.thing.Message;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MessageDao {
    private final MessageMapper mapper;

    public MessageDao(MessageMapper mapper) {
        this.mapper = mapper;
    }

    public void insert(Message message) {
        mapper.insertMessage(message);
    }

    public Optional<Message> findById(Long messageId) {
        return Optional.ofNullable(mapper.selectById(messageId));
    }

    public Optional<Message> findByCode(String messageCode) {
        return Optional.ofNullable(mapper.selectByCode(messageCode));
    }

    public List<Message> findMessages(MessageSearchCondition condition) {
        return mapper.selectMessages(condition);
    }

    public long countMessages(MessageSearchCondition condition) {
        return mapper.countMessages(condition);
    }
}
